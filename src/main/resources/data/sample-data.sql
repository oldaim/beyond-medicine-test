-- AppVersion 데이터 생성 (OS와 모드에 따라 총 4개)
INSERT INTO app_version (latest_version, minimum_version, os, mode, hash) 
VALUES ('1.2.0', '1.0.0', 'android', 'debug', '370901f93faca101b6a15d64325bb0d93de06cad06cbfd41ca196891c4edb145');

INSERT INTO app_version (latest_version, minimum_version, os, mode, hash) 
VALUES ('1.2.0', '1.0.0', 'android', 'release', 'd6b08169ed863771aaa8c3b23314e3bd0b6d633e713e50d2a40517747fc0e1a6');

INSERT INTO app_version (latest_version, minimum_version, os, mode, hash) 
VALUES ('1.2.0', '1.0.0', 'ios', 'debug', 'e9ab09e9c39f816c9860b2ecec3595e1c2e166088893fcb3b8459cae6211aae9');

INSERT INTO app_version (latest_version, minimum_version, os, mode, hash) 
VALUES ('1.2.0', '1.0.0', 'ios', 'release', '1adaa48972f1e42b3bcb4f8cd6188dd76bee2f1a8e9a4edb9b9d75aa551fc384');

-- AccessCodeHistory 데이터 생성 (10개)
INSERT INTO access_code_history (hospital_id, access_code, created_at) 
VALUES ('JDQ4MTg4MSM1MSMkMSMkMCMkNzIkNTgxOTYxIzMxIyQxIyQ3IyQ4OSQyNjEwMDIjNTEjJDEjJDIjJDgz', 'ABCD1234', '2025-03-10 10:15:00');

INSERT INTO access_code_history (hospital_id, access_code, created_at) 
VALUES ('JDQ4MTg4MSM1MSMkMSMkMCMkNzIkNTgxOTYxIzMxIyQxIyQ3IyQ4OSQyNjEwMDIjNTEjJDEjJDIjJDgz', 'WXYZ9876', '2025-03-11 14:30:00');

INSERT INTO access_code_history (hospital_id, access_code, created_at) 
VALUES ('PFA6N3IyOSM1MSMkMSMkMCMkNzIkNTgxOTYxIzMxIyQxIyQ3IyQ4OSQyNjEwMDIjNTEjJDEjJDIjJDgz', 'QWER5678', '2025-03-12 09:45:00');

INSERT INTO access_code_history (hospital_id, access_code, created_at) 
VALUES ('JFI1O2pyOSM1MSMkMSMkMCMkNzIkNTgxOTYxIzMxIyQxIyQ3IyQ4OSQyNjEwMDIjNTEjJDEjJDIjJDgz', 'ASDF4321', '2025-03-13 11:20:00');

INSERT INTO access_code_history (hospital_id, access_code, created_at) 
VALUES ('QFEwOHRqOSM1MSMkMSMkMCMkNzIkNTgxOTYxIzMxIyQxIyQ3IyQ4OSQyNjEwMDIjNTEjJDEjJDIjJDgz', 'ZXCV1357', '2025-03-14 16:10:00');

INSERT INTO access_code_history (hospital_id, access_code, created_at) 
VALUES ('UGV0O2pyOSM1MSMkMSMkMCMkNzIkNTgxOTYxIzMxIyQxIyQ3IyQ4OSQyNjEwMDIjNTEjJDEjJDIjJDgz', 'POIU2468', '2025-03-15 13:25:00');

INSERT INTO access_code_history (hospital_id, access_code, created_at) 
VALUES ('R3V0O2pyOSM1MSMkMSMkMCMkNzIkNTgxOTYxIzMxIyQxIyQ3IyQ4OSQyNjEwMDIjNTEjJDEjJDIjJDgz', 'LKJH8642', '2025-03-16 10:50:00');

INSERT INTO access_code_history (hospital_id, access_code, created_at) 
VALUES ('SGV0O2pyOSM1MSMkMSMkMCMkNzIkNTgxOTYxIzMxIyQxIyQ3IyQ4OSQyNjEwMDIjNTEjJDEjJDIjJDgz', 'MNBV7531', '2025-03-17 15:40:00');

INSERT INTO access_code_history (hospital_id, access_code, created_at) 
VALUES ('VGV0O2pyOSM1MSMkMSMkMCMkNzIkNTgxOTYxIzMxIyQxIyQ3IyQ4OSQyNjEwMDIjNTEjJDEjJDIjJDgz', 'GHYJ3579', '2025-03-18 12:15:00');

INSERT INTO access_code_history (hospital_id, access_code, created_at) 
VALUES ('WGV0O2pyOSM1MSMkMSMkMCMkNzIkNTgxOTYxIzMxIyQxIyQ3IyQ4OSQyNjEwMDIjNTEjJDEjJDIjJDgz', 'TYUI8024', '2025-03-19 14:05:00');

-- UserAccessCode 데이터 생성 (10개) - 5개는 ACTIVE, 5개는 EXPIRED 상태
INSERT INTO user_access_code (user_id, access_code, status, activated_at, expires_at) 
VALUES ('e4e3ecbd-2208-4905-8120-426473d0eae9', 'ABCD1234', 'ACTIVE', '2025-03-10 10:30:00', '2025-04-22 00:00:00');

INSERT INTO user_access_code (user_id, access_code, status, activated_at, expires_at) 
VALUES ('f5f4fdce-3319-5a16-9231-537584e1fbf0', 'WXYZ9876', 'ACTIVE', '2025-03-11 15:00:00', '2025-04-23 00:00:00');

INSERT INTO user_access_code (user_id, access_code, status, activated_at, expires_at) 
VALUES ('a1a2a3a4-5678-90ab-cdef-1234567890ab', 'QWER5678', 'ACTIVE', '2025-03-12 10:15:00', '2025-04-24 00:00:00');

INSERT INTO user_access_code (user_id, access_code, status, activated_at, expires_at) 
VALUES ('b1b2b3b4-5678-90ab-cdef-1234567890cd', 'ASDF4321', 'ACTIVE', '2025-03-13 11:45:00', '2025-04-25 00:00:00');

INSERT INTO user_access_code (user_id, access_code, status, activated_at, expires_at) 
VALUES ('c1c2c3c4-5678-90ab-cdef-1234567890ef', 'ZXCV1357', 'ACTIVE', '2025-03-14 16:30:00', '2025-04-26 00:00:00');

INSERT INTO user_access_code (user_id, access_code, status, activated_at, expires_at) 
VALUES ('d1d2d3d4-5678-90ab-cdef-123456789012', 'POIU2468', 'EXPIRED', '2025-01-15 13:45:00', '2025-02-27 00:00:00');

INSERT INTO user_access_code (user_id, access_code, status, activated_at, expires_at) 
VALUES ('e1e2e3e4-5678-90ab-cdef-123456789034', 'LKJH8642', 'EXPIRED', '2025-01-16 11:10:00', '2025-02-28 00:00:00');

INSERT INTO user_access_code (user_id, access_code, status, activated_at, expires_at) 
VALUES ('f1f2f3f4-5678-90ab-cdef-123456789056', 'MNBV7531', 'EXPIRED', '2025-01-17 16:00:00', '2025-03-01 00:00:00');

INSERT INTO user_access_code (user_id, access_code, status, activated_at, expires_at) 
VALUES ('a2b3c4d5-6789-0abc-def1-23456789abcd', 'GHYJ3579', 'EXPIRED', '2025-01-18 12:30:00', '2025-03-02 00:00:00');

INSERT INTO user_access_code (user_id, access_code, status, activated_at, expires_at) 
VALUES ('e5f6g7h8-90ab-cdef-1234-56789abcdef0', 'TYUI8024', 'EXPIRED', '2025-01-19 14:20:00', '2025-03-03 00:00:00');

-- UserVerificationLog 데이터 생성 (10개)
INSERT INTO user_verification_log (user_id, version, os, mode, hash, requested_at) 
VALUES ('e4e3ecbd-2208-4905-8120-426473d0eae9', '1.1.0', 'android', 'debug', '370901f93faca101b6a15d64325bb0d93de06cad06cbfd41ca196891c4edb145', '2025-03-20 09:15:30');

INSERT INTO user_verification_log (user_id, version, os, mode, hash, requested_at) 
VALUES ('f5f4fdce-3319-5a16-9231-537584e1fbf0', '1.0.5', 'android', 'release', 'd6b08169ed863771aaa8c3b23314e3bd0b6d633e713e50d2a40517747fc0e1a6', '2025-03-20 10:23:45');

INSERT INTO user_verification_log (user_id, version, os, mode, hash, requested_at) 
VALUES ('a1a2a3a4-5678-90ab-cdef-1234567890ab', '1.1.2', 'ios', 'debug', 'wQ1BvC2DxRdE6PsLmKa9Tyo5Z3A=', '2025-03-20 11:34:12');

INSERT INTO user_verification_log (user_id, version, os, mode, hash, requested_at) 
VALUES ('b1b2b3b4-5678-90ab-cdef-1234567890cd', '1.0.8', 'ios', 'release', 'pW9qLxB7n4dYrEv2cGtH5sFj6kA=', '2025-03-20 12:45:30');

INSERT INTO user_verification_log (user_id, version, os, mode, hash, requested_at) 
VALUES ('c1c2c3c4-5678-90ab-cdef-1234567890ef', '0.9.9', 'android', 'debug', 'Y95ULTuEF0uXNq7fSNa1EEzP0FU=', '2025-03-20 13:56:22');

INSERT INTO user_verification_log (user_id, version, os, mode, hash, requested_at) 
VALUES ('d1d2d3d4-5678-90ab-cdef-123456789012', '1.2.0-beta.1', 'android', 'release', 'KzUF9SrDl1Znq8qbfTmOLiH4f0M=', '2025-03-20 14:10:05');

INSERT INTO user_verification_log (user_id, version, os, mode, hash, requested_at) 
VALUES ('e1e2e3e4-5678-90ab-cdef-123456789034', '1.1.5-alpha.2', 'ios', 'debug', 'wQ1BvC2DxRdE6PsLmKa9Tyo5Z3A=', '2025-03-20 15:22:18');

INSERT INTO user_verification_log (user_id, version, os, mode, hash, requested_at) 
VALUES ('f1f2f3f4-5678-90ab-cdef-123456789056', '1.1.9', 'ios', 'release', 'pW9qLxB7n4dYrEv2cGtH5sFj6kA=', '2025-03-20 16:33:41');

INSERT INTO user_verification_log (user_id, version, os, mode, hash, requested_at) 
VALUES ('a2b3c4d5-6789-0abc-def1-23456789abcd', '1.0.1', 'android', 'debug', 'Y95ULTuEF0uXNq7fSNa1EEzP0FU=', '2025-03-20 17:44:52');

INSERT INTO user_verification_log (user_id, version, os, mode, hash, requested_at) 
VALUES ('e5f6g7h8-90ab-cdef-1234-56789abcdef0', '1.0.3', 'android', 'release', 'KzUF9SrDl1Znq8qbfTmOLiH4f0M=', '2025-03-20 18:55:07');
