# Submit Idempotency Change (Backup Note)

Date: 2026-03-31

## Summary
Changed quiz submit to be idempotent. If a quiz instance is already `SUBMITTED` or `TIMED_OUT`, the API returns the existing result instead of throwing 409/400. This reduces failures during concurrent submit attempts.

## Files Changed
- `src/main/java/com/tomzxy/web_quiz/services/impl/QuizInstanceServiceImpl.java`

## Behavior Before
- Submit always tried to lock.
- If lock was held, returned 409.
- If status was not `IN_PROGRESS`, returned 400.

## Behavior After
- If status is `SUBMITTED` or `TIMED_OUT`, return result immediately.
- If lock is held, re-check status; return result if completed, else 409.

## Rollback
To revert, restore the old `submitQuiz` logic:
- Remove the early status checks for `SUBMITTED`/`TIMED_OUT`.
- Always acquire the lock first and throw on conflict.
- Throw 400 when status is not `IN_PROGRESS`.
