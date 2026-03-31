import http from 'k6/http';
import { sleep, check } from 'k6';

// ===== CONFIG =====
const BASE_URL = 'http://localhost:8080/api';
const USER_COUNT = 500;
const DEFAULT_PASSWORD = '123456';
const USERS = Array.from({ length: USER_COUNT }, (_, i) => ({
  email: `user${i + 1}@example.com`,
  password: DEFAULT_PASSWORD,
}));

// ===== OPTIONS =====
export const options = {
  vus: 200,
  duration: '30s',
};

// ===== HELPER =====
function randomAnswerIndex(max) {
  if (max <= 0) return 0;
  return Math.floor(Math.random() * max);
}

function logIfFail(res, stepName) {
  if (!res) {
    console.log(`[${stepName}] No response received`);
    return;
  }
  if (res.status >= 400 || res.status === 0) {
    // cắt body chỉ lấy 500 ký tự đầu
    const bodyPreview = res.body ? res.body.substring(0, 500) : '';
    console.log(`[${stepName}] Request failed
  URL: ${res.url}
  Status: ${res.status}
  Body: ${bodyPreview}`);
  }
}

function getPageItems(res) {
  if (!res || res.status !== 200) return [];
  const body = JSON.parse(res.body || '{}');
  const page = body.items || {};
  return page.items || [];
}

function pickGroupId(authHeaders) {
  // Joined groups
  let res = http.get(`${BASE_URL}/groups/joined?page=0&size=50`, authHeaders);
  logIfFail(res, 'groups-joined');
  let groups = getPageItems(res);
  if (groups.length) return groups[0].id;

  // Owned groups
  res = http.get(`${BASE_URL}/groups/owned?page=0&size=50`, authHeaders);
  logIfFail(res, 'groups-owned');
  groups = getPageItems(res);
  if (groups.length) return groups[0].id;

  // All groups -> try join first
  res = http.get(`${BASE_URL}/groups?page=0&size=50`, authHeaders);
  logIfFail(res, 'groups-all');
  groups = getPageItems(res);
  if (!groups.length) return null;

  const groupId = groups[0].id;
  const joinRes = http.post(
    `${BASE_URL}/groups/join`,
    JSON.stringify({ lobbyId: groupId }),
    authHeaders
  );
  logIfFail(joinRes, 'groups-join');
  if (joinRes.status !== 200) return null;
  return groupId;
}

function pickGroupQuizId(groupId, authHeaders) {
  const res = http.get(`${BASE_URL}/groups/${groupId}/quizzes?page=0&size=50`, authHeaders);
  logIfFail(res, 'group-quizzes');
  const quizzes = getPageItems(res);
  if (!quizzes.length) return null;
  const openedQuiz = quizzes.find((q) => q.status === 'OPENED');
  return (openedQuiz || quizzes[0]).id;
}

// ===== USER FLOW =====
function userFlow() {
  const user = USERS[__VU - 1];

  // 1. login
  let loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
    email: user.email,
    password: user.password,
  }), {
    headers: { 'Content-Type': 'application/json' },
  });
  logIfFail(loginRes, 'login');
  if (!check(loginRes, { 'login success': (r) => r.status === 200 })) {
    return;
  }

  const loginBody = JSON.parse(loginRes.body);
  const token = loginBody.items ? loginBody.items.token : null;
  if (!token) return;

  const authHeaders = {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  };

  // 2. get group -> pick groupId
  const groupId = pickGroupId(authHeaders);
  if (!groupId) return;

  // 3. get quizzes in group -> pick quizId
  const quizId = pickGroupQuizId(groupId, authHeaders);
  if (!quizId) return;

  // 4. start quiz
  let startRes = http.post(`${BASE_URL}/quizzes/${quizId}/start`, null, authHeaders);
  logIfFail(startRes, 'start');
  if (!check(startRes, { 'start success': (r) => r.status === 201 })) return;

  const instance = JSON.parse(startRes.body).items;
  const attemptId = instance ? instance.id : null;
  if (!attemptId) return;

  sleep(0.5 + Math.random() * 1.5);

  // 5. answer questions
  const questions = instance.questions || [];
  for (let i = 0; i < questions.length; i++) {
    const q = questions[i];
    const answersCount = (q.answers || []).length;
    const answerIndex = randomAnswerIndex(answersCount);

    const answerRes = http.post(`${BASE_URL}/quiz-instances/${attemptId}/answer`, JSON.stringify({
      questionSnapshotKey: q.snapshotKey,
      answer: [answerIndex],
    }), authHeaders);
    logIfFail(answerRes, `answer-${i}`);

    sleep(Math.random());
  }

  // 6. submit with retry
  const maxSubmitAttempts = 3;
  let submitRes = null;
  for (let i = 0; i < maxSubmitAttempts; i++) {
    submitRes = http.post(`${BASE_URL}/quiz-instances/${attemptId}/submit`, null, authHeaders);
    logIfFail(submitRes, 'submit');
    if (submitRes.status === 200) break;
    if (submitRes.status === 409 || submitRes.status >= 500) {
      sleep(0.3 + Math.random() * 0.5);
      continue;
    }
    break;
  }
  check(submitRes, { 'submit success': (r) => r && r.status === 200 });
}

// ===== MAIN =====
export default function () {
  userFlow();
  sleep(1);
}
