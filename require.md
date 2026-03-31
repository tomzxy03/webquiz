flow: login -> groups -> groupId -> quizzes -> quizId -> start -> answer question -> submit
cenarios: (100.00%) 1 scenario, 500 max VUs, 2m30s max duration (incl. graceful stop):
              * default: 500 looping VUs for 2m0s (gracefulStop: 30s)

INFO[0104] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/13/submit
  Status: 500
  Body: {"code":500,"message":"JDBC exception executing SQL [select qur1_0.id,qur1_0.answered_at,qur1_0.created_at,qur1_0.is_active,qur1_0.is_correct,qur1_0.is_skipped,qur1_0.points_earned,qur1_0.question_id,qur1_0.question_snapshot_key,qur1_0.quiz_instance_id,qur1_0.response_time_seconds,qur1_0.selected_answer_id,qur1_0.selected_answer_ids,qur1_0.updated_at,qur1_0.uuid from quiz_user_responses qur1_0 where qur1_0.quiz_instance_id=? and qur1_0.is_active=true] [ERROR: current transaction is aborted, comm  source=console
INFO[0104] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/7/submit
  Status: 500
  Body: {"code":500,"message":"JDBC exception executing SQL [select qur1_0.id,qur1_0.answered_at,qur1_0.created_at,qur1_0.is_active,qur1_0.is_correct,qur1_0.is_skipped,qur1_0.points_earned,qur1_0.question_id,qur1_0.question_snapshot_key,qur1_0.quiz_instance_id,qur1_0.response_time_seconds,qur1_0.selected_answer_id,qur1_0.selected_answer_ids,qur1_0.updated_at,qur1_0.uuid from quiz_user_responses qur1_0 where qur1_0.quiz_instance_id=? and qur1_0.is_active=true] [ERROR: current transaction is aborted, comm  source=console
INFO[0104] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/10/submit
  Status: 500
  Body: {"code":500,"message":"JDBC exception executing SQL [select qur1_0.id,qur1_0.answered_at,qur1_0.created_at,qur1_0.is_active,qur1_0.is_correct,qur1_0.is_skipped,qur1_0.points_earned,qur1_0.question_id,qur1_0.question_snapshot_key,qur1_0.quiz_instance_id,qur1_0.response_time_seconds,qur1_0.selected_answer_id,qur1_0.selected_answer_ids,qur1_0.updated_at,qur1_0.uuid from quiz_user_responses qur1_0 where qur1_0.quiz_instance_id=? and qur1_0.is_active=true] [ERROR: current transaction is aborted, comm  source=console
INFO[0104] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/86/submit
  Status: 500
  Body: {"code":500,"message":"JDBC exception executing SQL [select qur1_0.id,qur1_0.answered_at,qur1_0.created_at,qur1_0.is_active,qur1_0.is_correct,qur1_0.is_skipped,qur1_0.points_earned,qur1_0.question_id,qur1_0.question_snapshot_key,qur1_0.quiz_instance_id,qur1_0.response_time_seconds,qur1_0.selected_answer_id,qur1_0.selected_answer_ids,qur1_0.updated_at,qur1_0.uuid from quiz_user_responses qur1_0 where qur1_0.quiz_instance_id=? and qur1_0.is_active=true] [ERROR: current transaction is aborted, comm  source=console
INFO[0104] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/42/submit
  Status: 500
  Body: {"code":500,"message":"JDBC exception executing SQL [select qur1_0.id,qur1_0.answered_at,qur1_0.created_at,qur1_0.is_active,qur1_0.is_correct,qur1_0.is_skipped,qur1_0.points_earned,qur1_0.question_id,qur1_0.question_snapshot_key,qur1_0.quiz_instance_id,qur1_0.response_time_seconds,qur1_0.selected_answer_id,qur1_0.selected_answer_ids,qur1_0.updated_at,qur1_0.uuid from quiz_user_responses qur1_0 where qur1_0.quiz_instance_id=? and qur1_0.is_active=true] [ERROR: current transaction is aborted, comm  source=console
INFO[0104] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/30/submit
  Status: 500
  Body: {"code":500,"message":"JDBC exception executing SQL [select qur1_0.id,qur1_0.answered_at,qur1_0.created_at,qur1_0.is_active,qur1_0.is_correct,qur1_0.is_skipped,qur1_0.points_earned,qur1_0.question_id,qur1_0.question_snapshot_key,qur1_0.quiz_instance_id,qur1_0.response_time_seconds,qur1_0.selected_answer_id,qur1_0.selected_answer_ids,qur1_0.updated_at,qur1_0.uuid from quiz_user_responses qur1_0 where qur1_0.quiz_instance_id=? and qur1_0.is_active=true] [ERROR: current transaction is aborted, comm  source=console
INFO[0104] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/53/submit
  Status: 500
  Body: {"code":500,"message":"JDBC exception executing SQL [select qur1_0.id,qur1_0.answered_at,qur1_0.created_at,qur1_0.is_active,qur1_0.is_correct,qur1_0.is_skipped,qur1_0.points_earned,qur1_0.question_id,qur1_0.question_snapshot_key,qur1_0.quiz_instance_id,qur1_0.response_time_seconds,qur1_0.selected_answer_id,qur1_0.selected_answer_ids,qur1_0.updated_at,qur1_0.uuid from quiz_user_responses qur1_0 where qur1_0.quiz_instance_id=? and qur1_0.is_active=true] [ERROR: current transaction is aborted, comm  source=console
INFO[0135] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/102/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0135] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/285/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0135] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/477/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=190)] [n/a]","items":null}  source=console
INFO[0135] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/140/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30001ms (total=10, active=10, idle=0, waiting=189)] [n/a]","items":null}  source=console
INFO[0135] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/5/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0135] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/148/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0135] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/439/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=190)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/309/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=189)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/270/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/472/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/171/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/454/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=190)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/449/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/264/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/384/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=190)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/375/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-7] Request failed
  URL: http://localhost:8080/api/quiz-instances/480/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=189)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/334/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=188)] [n/a]","items":null}  source=console
INFO[0136] ❌ [groups-joined] Request failed
  URL: http://localhost:8080/api/groups/joined?page=0&size=50
  Status: 403
  Body:   source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/292/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/135/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/378/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/310/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/175/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=189)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/294/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=190)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/232/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=188)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/418/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30001ms (total=10, active=10, idle=0, waiting=186)] [n/a]","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/20/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=185)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/329/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=183)] [n/a]","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/237/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=187)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/427/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/296/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/343/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=184)] [n/a]","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/37/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30001ms (total=10, active=10, idle=0, waiting=183)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/198/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=183)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/338/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/133/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/486/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=182)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/256/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30018ms (total=10, active=10, idle=0, waiting=181)] [n/a]","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/122/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30001ms (total=10, active=10, idle=0, waiting=179)] [n/a]","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/194/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=181)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/260/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30007ms (total=10, active=10, idle=0, waiting=181)] [n/a]","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/168/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/50/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=177)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-7] Request failed
  URL: http://localhost:8080/api/quiz-instances/467/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/473/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=180)] [n/a]","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/49/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/474/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/84/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/40/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30001ms (total=10, active=10, idle=0, waiting=177)] [n/a]","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/136/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/319/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=190)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/271/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=190)] [n/a]","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/55/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/263/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0136] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/114/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30001ms (total=10, active=10, idle=0, waiting=189)] [n/a]","items":null}  source=console
INFO[0136] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/289/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=189)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/137/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/119/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/143/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=187)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/72/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=186)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/117/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/284/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/32/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/340/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/159/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/200/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/98/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30007ms (total=10, active=10, idle=0, waiting=183)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/241/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=184)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/411/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/357/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=181)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/354/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30006ms (total=10, active=10, idle=0, waiting=181)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/229/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30013ms (total=10, active=10, idle=0, waiting=182)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/407/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/234/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=181)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/476/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30019ms (total=10, active=10, idle=0, waiting=181)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/145/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30005ms (total=10, active=10, idle=0, waiting=180)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/221/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30004ms (total=10, active=10, idle=0, waiting=181)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/409/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=178)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/420/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=179)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/62/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=180)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/278/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/66/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30004ms (total=10, active=10, idle=0, waiting=178)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/339/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=179)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/203/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30001ms (total=10, active=10, idle=0, waiting=177)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/327/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30001ms (total=10, active=10, idle=0, waiting=179)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/173/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=185)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/317/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/139/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/419/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=190)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/364/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=187)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/81/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30001ms (total=10, active=10, idle=0, waiting=186)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/197/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/176/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/287/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/353/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/405/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=186)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/183/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/53/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/269/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/13/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/130/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=173)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/26/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=179)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/410/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30006ms (total=10, active=10, idle=0, waiting=178)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/242/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/421/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/169/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/466/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=177)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/178/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/225/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=181)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/223/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=183)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/153/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30006ms (total=10, active=10, idle=0, waiting=176)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/149/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=182)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/86/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/441/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30001ms (total=10, active=10, idle=0, waiting=168)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/33/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-7] Request failed
  URL: http://localhost:8080/api/quiz-instances/482/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/366/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/385/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=169)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/7/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/8/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30010ms (total=10, active=10, idle=0, waiting=167)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/401/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30005ms (total=10, active=10, idle=0, waiting=167)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/377/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=166)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/58/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30020ms (total=10, active=10, idle=0, waiting=163)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-7] Request failed
  URL: http://localhost:8080/api/quiz-instances/448/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30006ms (total=10, active=10, idle=0, waiting=165)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/110/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=167)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/424/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=168)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/295/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=166)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/391/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/360/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/65/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30005ms (total=10, active=10, idle=0, waiting=165)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/341/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30010ms (total=10, active=10, idle=0, waiting=164)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/186/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/408/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30013ms (total=10, active=10, idle=0, waiting=169)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/78/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30006ms (total=10, active=10, idle=0, waiting=161)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/437/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30007ms (total=10, active=10, idle=0, waiting=161)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/440/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=162)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/217/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=161)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/202/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=166)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/403/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/10/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/374/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-2] Request failed
  URL: http://localhost:8080/api/quiz-instances/487/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/24/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/59/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/74/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=174)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/189/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [group-quizzes] Request failed
  URL: http://localhost:8080/api/groups/15/quizzes?page=0&size=50
  Status: 500
  Body: {"code":500,"message":"Could not open JPA EntityManager for transaction","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/38/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=170)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/342/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=169)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/127/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=169)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/151/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30015ms (total=10, active=10, idle=0, waiting=167)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/345/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/352/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/251/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30002ms (total=10, active=10, idle=0, waiting=174)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/444/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30003ms (total=10, active=10, idle=0, waiting=171)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/395/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/245/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30021ms (total=10, active=10, idle=0, waiting=174)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/161/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/42/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/224/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=175)] [n/a]","items":null}  source=console
INFO[0137] ❌ [login] Request failed
  URL: http://localhost:8080/api/auth/login
  Status: 500
  Body: {"code":500,"message":"Could not open JPA EntityManager for transaction","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/470/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/123/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/144/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/88/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30036ms (total=10, active=10, idle=0, waiting=176)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/458/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30005ms (total=10, active=10, idle=0, waiting=175)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/351/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/478/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=171)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/199/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/336/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/30/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/318/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/268/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/299/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/182/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/283/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/330/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30000ms (total=10, active=10, idle=0, waiting=171)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/113/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30044ms (total=10, active=10, idle=0, waiting=168)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/306/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/468/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30043ms (total=10, active=10, idle=0, waiting=167)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/286/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30051ms (total=10, active=10, idle=0, waiting=170)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/120/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30038ms (total=10, active=10, idle=0, waiting=169)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/105/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/426/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/87/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [groups-joined] Request failed
  URL: http://localhost:8080/api/groups/joined?page=0&size=50
  Status: 403
  Body:   source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/94/submit
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/192/answer
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30007ms (total=10, active=10, idle=0, waiting=161)] [n/a]","items":null}  source=console
INFO[0137] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/54/submit
  Status: 500
  Body: {"code":500,"message":"Unable to acquire JDBC Connection [HikariPool-1 - Connection is not available, request timed out after 30005ms (total=10, active=10, idle=0, waiting=183)] [n/a]","items":null}  source=console
INFO[0137] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/126/answer
  Status: 500
  Body: {"code":500,"message":"No valid identity found in request","items":null}  source=console
INFO[0150] ❌ [answer-8] Request failed
  URL: http://localhost:8080/api/quiz-instances/414/answer
  Status: 0
  Body:   source=console
INFO[0150] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/414/answer
  Status: 0
  Body:   source=console
INFO[0150] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/414/submit
  Status: 0
  Body:   source=console
INFO[0150] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/213/submit
  Status: 0
  Body:   source=console
INFO[0150] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/109/submit
  Status: 0
  Body:   source=console
INFO[0150] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/223/answer
  Status: 0
  Body:   source=console
INFO[0150] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/223/submit
  Status: 0
  Body:   source=console
INFO[0150] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/356/answer
  Status: 0
  Body:   source=console
INFO[0150] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/356/submit
  Status: 0
  Body:   source=console
INFO[0150] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/316/answer
  Status: 0
  Body:   source=console
INFO[0150] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/316/submit
  Status: 0
  Body:   source=console
INFO[0150] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/429/submit
  Status: 0
  Body:   source=console
INFO[0150] ❌ [login] Request failed
  URL: http://localhost:8080/api/auth/login
  Status: 0
  Body:   source=console
INFO[0150] ❌ [answer-9] Request failed
  URL: http://localhost:8080/api/quiz-instances/469/answer
  Status: 0
  Body:   source=console
INFO[0150] ❌ [submit] Request failed
  URL: http://localhost:8080/api/quiz-instances/469/submit
  Status: 0
  Body:   source=console


  █ TOTAL RESULTS 

    checks_total.......: 1090   7.265223/s
    checks_succeeded...: 99.90% 1089 out of 1090
    checks_failed......: 0.09%  1 out of 1090

    ✗ login success
      ↳  99% — ✓ 566 / ✗ 1
    ✓ start success
    ✓ submit success

    HTTP
    http_req_duration..............: avg=8.04s  min=83.81ms med=6.48s  max=41.14s p(90)=12.58s p(95)=17.98s
      { expected_response:true }...: avg=7.29s  min=83.81ms med=6.4s   max=23.81s p(90)=11.54s p(95)=14.45s
    http_req_failed................: 2.85%  198 out of 6936
    http_reqs......................: 6936   46.230815/s

    EXECUTION
    iteration_duration.............: avg=49.52s min=11.16s  med=26.64s max=1m48s  p(90)=1m47s  p(95)=1m47s 
    iterations.....................: 107    0.713192/s
    vus............................: 498    min=498         max=500
    vus_max........................: 500    min=500         max=500

    NETWORK
    data_received..................: 6.5 MB 43 kB/s
    data_sent......................: 4.1 MB 27 kB/s




running (2m30.0s), 000/500 VUs, 107 complete and 498 interrupted iterations
default ✓ [======================================] 500 VUs  2m0s
