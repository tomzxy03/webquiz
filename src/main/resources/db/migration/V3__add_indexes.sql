-- =====================================================
-- INDEXES FOR PERFORMANCE OPTIMIZATION
-- =====================================================

-- 1. USER INDEXES
CREATE INDEX idx_user_email ON public.users(email);
CREATE INDEX idx_user_username ON public.users(user_name);

-- 2. QUESTION & ANSWER INDEXES
CREATE INDEX idx_question_bank ON public.questions(bank_id);
CREATE INDEX idx_question_folder ON public.questions(folder_id);
CREATE INDEX idx_question_bank_folder ON public.questions(bank_id, folder_id);
CREATE INDEX idx_question_level ON public.questions(level);
CREATE INDEX idx_question_type ON public.questions(question_type);
CREATE INDEX idx_question_created_at ON public.questions(created_at);
CREATE INDEX idx_answer_question ON public.answers(question_id);
CREATE INDEX idx_answer_correct ON public.answers(answer_correct);

-- 3. QUIZ INDEXES
CREATE INDEX idx_quiz_host ON public.quizzes(host_id);
CREATE INDEX idx_quiz_subject ON public.quizzes(subject_id);
CREATE INDEX idx_quiz_visibility ON public.quizzes(visibility);
CREATE INDEX idx_quiz_created_at ON public.quizzes(created_at);

-- 4. QUIZ INSTANCE INDEXES
CREATE INDEX idx_quiz_instance_quiz ON public.quiz_instances(quiz_id);
CREATE INDEX idx_quiz_instance_user ON public.quiz_instances(user_id);
CREATE INDEX idx_quiz_instance_started ON public.quiz_instances(started_at);
CREATE INDEX idx_quiz_status_user ON public.quiz_instances(quiz_id, user_id, status);

-- 5. QUIZ USER RESPONSE INDEXES
CREATE INDEX idx_quiz_user_response_instance ON public.quiz_user_responses(quiz_instance_id);
CREATE INDEX idx_quiz_user_response_instance_answered ON public.quiz_user_responses(quiz_instance_id, answered_at);
CREATE INDEX idx_quiz_user_response_instance_correct ON public.quiz_user_responses(quiz_instance_id, is_correct);

-- 6. LOBBY & MEMBERSHIP INDEXES
CREATE INDEX idx_lobby_member_lobby ON public.lobby_members(lobby_id);
CREATE INDEX idx_lobby_member_user ON public.lobby_members(user_id);
CREATE INDEX idx_lobby_member_lobby_user ON public.lobby_members(lobby_id, user_id);

-- 7. QUESTION BANK & FOLDER INDEXES
CREATE INDEX idx_bank_owner ON public.question_banks(owner_id);
CREATE INDEX idx_folder_hierarchy ON public.folder(parent_id, bank_id);
CREATE INDEX idx_questions_folder_lookup ON public.questions(folder_id, bank_id);

-- 8. ROLE & PERMISSION INDEXES
CREATE INDEX idx_user_roles_user_id ON public.user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON public.user_roles(role_id);

-- 9. REFRESH TOKEN INDEXES
CREATE INDEX idx_user_id ON public.refresh_tokens(user_id);
CREATE INDEX idx_expired_at ON public.refresh_tokens(expired_at);

-- =====================================================
-- UNIQUE CONSTRAINTS
-- =====================================================

-- Folder name uniqueness at the same level
CREATE UNIQUE INDEX uk_folder_name_level ON public.folder(name, parent_id) WHERE parent_id IS NOT NULL;
CREATE UNIQUE INDEX uk_folder_name_root ON public.folder(name, bank_id) WHERE parent_id IS NULL;

-- Quiz instance constraint: prevent multiple IN_PROGRESS attempts
CREATE UNIQUE INDEX uk_active_attempt_per_user ON public.quiz_instances(user_id, quiz_id) WHERE (status = 'IN_PROGRESS');

-- Quiz user response unique constraint
ALTER TABLE ONLY public.quiz_user_responses ADD CONSTRAINT uk_instance_question UNIQUE (quiz_instance_id, question_id);

-- Quiz instance in-progress constraint
ALTER TABLE ONLY public.quiz_instances ADD CONSTRAINT uk_quiz_user_inprogress UNIQUE (quiz_id, user_id, status);

-- Refresh token JTI uniqueness
ALTER TABLE ONLY public.refresh_tokens ADD CONSTRAINT idx_jti UNIQUE (jti);

-- Question content hash uniqueness
ALTER TABLE ONLY public.questions ADD CONSTRAINT uk22uvhsu2cnsb404orqsl6gqdh UNIQUE (content_hash);