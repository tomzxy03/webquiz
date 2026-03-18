-- =====================================================
-- FOREIGN KEY CONSTRAINTS
-- =====================================================

-- Foreign Keys cho Folder & QuestionBank
ALTER TABLE ONLY public.question_banks ADD CONSTRAINT fk7gwo94ifvmd9gcftuxqt9ugeh FOREIGN KEY (owner_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.folder ADD CONSTRAINT fkn0cjh1seljcp0mc4tj1ufh99m FOREIGN KEY (parent_id) REFERENCES public.folder(id);
ALTER TABLE ONLY public.folder ADD CONSTRAINT fk6v3m3iyogdnix80g1btief7y FOREIGN KEY (bank_id) REFERENCES public.question_banks(owner_id);

-- Foreign Keys cho Questions & Answers
ALTER TABLE ONLY public.questions ADD CONSTRAINT fkep9feemoosshrpb95idh0xw1q FOREIGN KEY (bank_id) REFERENCES public.question_banks(owner_id);
ALTER TABLE ONLY public.questions ADD CONSTRAINT fkab3qxet4lhtxl7x8ydifoslw5 FOREIGN KEY (folder_id) REFERENCES public.folder(id);
ALTER TABLE ONLY public.answers ADD CONSTRAINT fk3erw1a3t0r78st8ty27x6v3g1 FOREIGN KEY (question_id) REFERENCES public.questions(id);

-- Foreign Keys cho Quizzes & Instances
ALTER TABLE ONLY public.quizzes ADD CONSTRAINT fk8usn959vod7g5aujcjt3mxa1m FOREIGN KEY (host_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.quizzes ADD CONSTRAINT fkk3pmfnkimtfaxsg0ag8id7i92 FOREIGN KEY (subject_id) REFERENCES public.subject(id);
ALTER TABLE ONLY public.quizzes ADD CONSTRAINT fkqjy15pked6p13vkt18ivevbc7 FOREIGN KEY (lobby_id) REFERENCES public.lobbies(id);

-- Foreign Keys cho Quiz Instances
ALTER TABLE ONLY public.quiz_instances ADD CONSTRAINT fknhhnbogiy0laqial2xgwrxgbx FOREIGN KEY (quiz_id) REFERENCES public.quizzes(id);
ALTER TABLE ONLY public.quiz_instances ADD CONSTRAINT fk3gu68kfy7mebise22jhhlulrw FOREIGN KEY (user_id) REFERENCES public.users(id);

-- Foreign Keys cho Quiz Links & Responses
ALTER TABLE ONLY public.quiz_question_links ADD CONSTRAINT fka25q7nsyyvcmv35j9m9u8cbed FOREIGN KEY (quiz_id) REFERENCES public.quizzes(id);
ALTER TABLE ONLY public.quiz_question_links ADD CONSTRAINT fkapn67meo3ccvc42u4me4ax4ys FOREIGN KEY (question_id) REFERENCES public.questions(id);
ALTER TABLE ONLY public.quiz_user_responses ADD CONSTRAINT fkkmm3o28nuv4oo9c6xypgflswm FOREIGN KEY (quiz_instance_id) REFERENCES public.quiz_instances(id);

-- Foreign Keys cho Lobbies & Members
ALTER TABLE ONLY public.lobbies ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (host_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.lobby_members ADD CONSTRAINT fkpn271v0upg7aqfwpu0j0i3n9o FOREIGN KEY (lobby_id) REFERENCES public.lobbies(id);
ALTER TABLE ONLY public.lobby_members ADD CONSTRAINT fkq5xry04b8nincqy0unxcsjus5 FOREIGN KEY (user_id) REFERENCES public.users(id);

-- Foreign Keys cho Roles & Permissions
ALTER TABLE ONLY public.user_roles ADD CONSTRAINT fk9y21adhxn0ayjhfocscqox7bh FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.user_roles ADD CONSTRAINT fkrhfovtciq1l558cw6udg0h0d3 FOREIGN KEY (role_id) REFERENCES public.role(id);
ALTER TABLE ONLY public.role_permission_object ADD CONSTRAINT fkcjh8wvsyt2v1g8cpnxn2hndw0 FOREIGN KEY (role_id) REFERENCES public.role(id);
ALTER TABLE ONLY public.role_permission_object ADD CONSTRAINT fkn8j79mscvpa505tdi8du1bpul FOREIGN KEY (permission_name) REFERENCES public.permission(permission_name);

-- Foreign Keys cho Notifications
ALTER TABLE ONLY public.notifications ADD CONSTRAINT fkbwr8wkd8alblvj0f3o4gbqno FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.notifications ADD CONSTRAINT fkku0xjuv3yydatips0sn2cb3ji FOREIGN KEY (lobby_id) REFERENCES public.lobbies(id);
ALTER TABLE ONLY public.user_notifications ADD CONSTRAINT fk9f86wonnl11hos1cuf5fibutl FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE ONLY public.user_notifications ADD CONSTRAINT fkovvx0ab3h8s9lrm6fppuadn7d FOREIGN KEY (notification_id) REFERENCES public.notifications(id);

-- Foreign Keys cho Refresh Tokens
ALTER TABLE ONLY public.refresh_tokens ADD CONSTRAINT fk1lih5y2npsf8u5o3vhdb9y0os FOREIGN KEY (user_id) REFERENCES public.users(id);