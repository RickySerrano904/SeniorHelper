-- =========================
-- Users (seniors, caregiver, admin)
-- =========================
INSERT INTO users (username, email, first_name, last_name, salt, hash, role) VALUES

--password for all users is: 'password'

--id: 1
('BaudelaireAdmin', 'baudelaire.admin@seniorhelper.local', 'Baudelaire', 'Admin', 'ydJ2wCfF1UyFUIGB+zPujU49znvnSephWCSUpKgsVX4=', 'yb0nfYHSbJ3M2xVVjCpXW1LYbvUDtPqdeQB9g4NTVE4=', 'ADMIN'),
--id: 2
('DayneAdmin', 'dayne.admin@seniorhelper.local', 'Dayne', 'Admin', '3npCHXX42Y7SwcDxniJBvM3x6nQ+8a0TonWKuw6nCsk=', 'zqaprzPMJDP6zMyGvYcCJrx5QP1gCj0pNj1MqhhQg9g=', 'ADMIN'),
--id: 3
('DeweyAdmin', 'dewey.admin@seniorhelper.local', 'Dewey', 'Admin', 'Wpd/YLoxqy+7sMTp0QnSx4ebOi53HFB2v4P7Mc8h8l4=', 'H/yX0mjIhX8idPV7/q5BgiOHEZ1HCD7q2QJya0NWsVg=', 'ADMIN'),
--id: 4
('TamiaAdmin', 'tamia.admin@seniorhelper.local', 'Tamia', 'Admin', 'd9kXezXEkjF0/t8AEycRy+gnhkXWBpE0LbqxSCrdsh4=', 'goSlUKo5izH/FqxjkpjErm8SP9zGHwUyF5t8B4REoGQ=', 'ADMIN'),
--id: 5
('LaurenAdmin', 'lauren.admin@seniorhelper.local', 'Lauren', 'Admin', 'R88ML2dHsCSeMGThU1TB0dBEi8HJsZPJQCYjubCKThA=', 'SHTkxE6sw7GCCcRzyLqApYyKpxc7uYK+p1wr57/A0IQ=', 'ADMIN'),
--id: 6
('RickyAdmin', 'ricky.admin@seniorhelper.local', 'Ricky', 'Admin', 'LHEMYdL7wXX1n/Te0MtZMIwaWB62tYX5vut3k7hrzyY=', 'XmjrUwL0Si9aqZYF0l+sRAhJCZiBEkWxDUdJsVrErgo=', 'ADMIN'),
--id: 7
('JohnSenior', 'john.senior@seniorhelper.local', 'John', 'Senior', 'xLIMpTWxayc83HWei6vBMoMlg2mYFhOBM/+/SY/kcYI=', '+Df1zgqiCFlAyTdoBHyqCXiib9RPLH6P8uUQb4WGs24=', 'SENIOR'),
--id: 8
('GertrudeSenior', 'gertrude.senior@seniorhelper.local', 'Gertrude', 'Senior', 'AgnahNewZ4qEU0MRqJChpZPMgSzQtl4PdlC7jy7qRBs=', 'YV8Uq0hZTlisg2RITbSk+uNXSD6LZ1oPRt4N1X5RdR4=', 'SENIOR'),
--id: 9
('TimSenior', 'tim.senior@seniorhelper.local', 'Tim', 'Senior', 'BMAVSc+jeTNzsFHcfhnUHeTjhgNZy6klGULmK1lFYn4=', 'rRNk/0Tq5JB1bN6mRvKNxRqyMZe4zjofZMJXOMOlLTc=', 'SENIOR'),
--id: 10
('DorothyCaregiver', 'dorothy.caregiver@seniorhelper.local', 'Dorothy', 'Caregiver', 'pkP4b5lTxL8HPqHu5MZVOZaJOeczVXvLZx0TidkEa6U=', 'ThiZ3KUEaaHhCArXkZjZtSUeFUu+7hQPOdoQjM+yvYo=', 'CAREGIVER'),
--id: 11
('FrankCaregiver', 'frank.caregiver@seniorhelper.local', 'Frank', 'Caregiver', 'YoFzz6jZVqaFQlKPnyf0AfH4K35ETFyC3J5eOeViG/w=', 'EONSwvav5XgS3BMzENg+RzH1WvO2oYGrN1z+PjRDzc4=', 'CAREGIVER'),
--id: 12
('BeatriceCaregiver', 'beatrice.caregiver@seniorhelper.local', 'Beatrice', 'Caregiver', 'GMdYfTLRVfSAPyPZmuAHSoCENAzIGeLsyzv3JOyzujw=', 'gRdPpzCQrGBJVIG2j8uMefVDLqs6k9M0VUYSM/xTVyg=', 'CAREGIVER');

-- =========================
-- Modules (6 - Updated to mimic Figma UI Prototype)
-- =========================
INSERT INTO modules (title, description)
VALUES
('Spotting Fake Messages', 'Learn how to recognize and avoid fake emails and scam messages'),
('Passwords & Privacy', 'Protect your accounts with better passwords and safe storage'),
('Device Defense', 'Keep your device secure with updates and basic protection steps'),
('Safe Shopping Online', 'How to tell if a website is safe before you purchase'),
('Imposter Scams', 'Avoid scams from fake tech support and government callers'),
('What to Do If Scammed', 'How to recover and protect your accounts after a scam');

-- =========================
-- Lessons (2 per module)
-- =========================
INSERT INTO lessons (title, description, module_id)
VALUES
-- Module 1 (Spotting Fake Messages)
('Lesson 1 - What Fake Messages Look Like', 'Learn the common signs of fake emails, texts, and online messages.', 1),
('Lesson 2 - What To Do When You Get One', 'Know the safest steps to take when you receive a suspicious message.', 1),

-- Module 2 (Passwords & Privacy)
('Lesson 1 - How to Create Strong Passwords', 'Learn the simple ways to create passwords that are hard to guess.', 2),
('Lesson 2 - Password Mistakes to Avoid', 'Avoid common habits that make accounts easy to break into.', 2),

-- Module 3 (Device Defense)
('Lesson 1 - Why Device Updates Matter', 'Understand how updates fix security problems and keep you protected.', 3),
('Lesson 2 - Turning On Automatic Updates', 'Learn how to enable automatic updates on your devices.', 3),

-- Module 4 (Safe Shopping Online)
('Lesson 1 - How to Tell if a Website Is Safe', 'Check for security signs before entering payment details.', 4),
('Lesson 2 - Safer Ways to Pay Online', 'Choose payment methods that give you fraud protection.', 4),

-- Module 5 (Imposter Scams)
('Lesson 1 - Impersonation Scams', 'Recognize how scammers pretend to be officials, tech support, or family members.', 5),
('Lesson 2 - How to Verify Who Contacted You', 'Use safe methods to confirm if a caller or sender is real.', 5),

-- Module 6 (What to Do If Scammed)
('Lesson 1 - What to Do Right Away', 'Take immediate steps to stop further damage, secure your accounts, and document what happened.', 6),
('Lesson 2 - Report, Recover, Prevent', 'Learn who to contact, how to recover your accounts, and prevention methods.', 6);

-- =========================
-- Quizzes (1 per module)
-- =========================
INSERT INTO quizzes (name, module_id)
VALUES
    ('Quiz - Recognizing Common Scam Tactics', 1),
    ('Quiz - Password Safety and Account Protection', 2);

-- =========================
-- Questions
-- =========================
-- Module 1 Quiz Questions
INSERT INTO questions (text, quiz_id) VALUES
    ('What is one clue that a text message might be fake?', 1),
    ('A friend asks for gift cards urgently by text. What should you do?', 1),
    ('Which behavior helps reduce text scam risk?', 1);

-- Module 2 Quiz Questions
INSERT INTO questions (text, quiz_id) VALUES
    ('Which URL looks the most legitimate for your bank?', 2),
    ('What does the lock icon (HTTPS) actually mean?',     2),
    ('Which of these is a red flag on a shopping site?',   2);

-- =========================
-- Answers
-- =========================

-- Quiz 1, Q1
INSERT INTO answers (text, correct, question_id) VALUES
('A. It uses your first and last name.', FALSE, (SELECT question_id FROM questions WHERE text = 'What is one clue that a text message might be fake?' AND quiz_id = 1)),
('B. It comes from a known number.', FALSE, (SELECT question_id FROM questions WHERE text = 'What is one clue that a text message might be fake?' AND quiz_id = 1)),
('C. It contains spelling mistakes', TRUE, (SELECT question_id FROM questions WHERE text = 'What is one clue that a text message might be fake?' AND quiz_id = 1));

-- Quiz 1, Q2
INSERT INTO answers (text, correct, question_id) VALUES
('A. Buy the cards and send the codes immediately', FALSE, (SELECT question_id FROM questions WHERE text = 'A friend asks for gift cards urgently by text. What should you do?' AND quiz_id = 1)),
('B. Call your friend using a trusted number to verify', TRUE, (SELECT question_id FROM questions WHERE text = 'A friend asks for gift cards urgently by text. What should you do?' AND quiz_id = 1)),
('C. Reply with your bank info to help them faster', FALSE, (SELECT question_id FROM questions WHERE text = 'A friend asks for gift cards urgently by text. What should you do?' AND quiz_id = 1));

-- Quiz 1, Q3
INSERT INTO answers (text, correct, question_id) VALUES
('A. Click links in any message that looks urgent', FALSE, (SELECT question_id FROM questions WHERE text = 'Which behavior helps reduce text scam risk?' AND quiz_id = 1)),
('B. Don’t reply to unknown senders and block/report spam', TRUE, (SELECT question_id FROM questions WHERE text = 'Which behavior helps reduce text scam risk?' AND quiz_id = 1)),
('C. Share your PIN if the message says it’s your bank', FALSE, (SELECT question_id FROM questions WHERE text = 'Which behavior helps reduce text scam risk?' AND quiz_id = 1));

-- Quiz 2, Q1
INSERT INTO answers (text, correct, question_id) VALUES
('A. bank.example.com.security-check.io', FALSE, (SELECT question_id FROM questions WHERE text = 'Which URL looks the most legitimate for your bank?' AND quiz_id = 2)),
('B. secure-bank-login.co', FALSE, (SELECT question_id FROM questions WHERE text = 'Which URL looks the most legitimate for your bank?' AND quiz_id = 2)),
('C. bank.example.com', TRUE, (SELECT question_id FROM questions WHERE text = 'Which URL looks the most legitimate for your bank?' AND quiz_id = 2));

-- Quiz 2, Q2
INSERT INTO answers (text, correct, question_id) VALUES
('A. The site is 100% trustworthy and safe', FALSE, (SELECT question_id FROM questions WHERE text = 'What does the lock icon (HTTPS) actually mean?' AND quiz_id = 2)),
('B. Your connection is encrypted, but you still must verify', TRUE, (SELECT question_id FROM questions WHERE text = 'What does the lock icon (HTTPS) actually mean?' AND quiz_id = 2)),
('C. The site is owned by the government', FALSE, (SELECT question_id FROM questions WHERE text = 'What does the lock icon (HTTPS) actually mean?' AND quiz_id = 2));

-- Quiz 2, Q3
INSERT INTO answers (text, correct, question_id) VALUES
('A. Typosquatted domain and unrealistic discounts', TRUE, (SELECT question_id FROM questions WHERE text = 'Which of these is a red flag on a shopping site?' AND quiz_id = 2)),
('B. Clear return policy and known payment options', FALSE, (SELECT question_id FROM questions WHERE text = 'Which of these is a red flag on a shopping site?' AND quiz_id = 2)),
('C. Contact information that matches official channels', FALSE, (SELECT question_id FROM questions WHERE text = 'Which of these is a red flag on a shopping site?' AND quiz_id = 2));

-- =========================
-- Lesson Completions
-- =========================
INSERT INTO lesson_completions (user_id, lesson_id, completed_at) VALUES
-- John finishes Lesson 1 from Module 1
(7, 1, CURRENT_TIMESTAMP),

-- Gertrude skips ahead and completes Lesson 1 from Module 2
(8, 3, CURRENT_TIMESTAMP);

-- =========================
-- Quiz Completions
-- =========================
INSERT INTO quiz_completions (user_id, quiz_id, completed_at) VALUES
-- John finishes Quiz 1 from Module 1
(7, 1, CURRENT_TIMESTAMP);

-- =========================
-- Appointments
-- =========================
INSERT INTO appointments (senior_id, title, location, notes, start, "end") VALUES
    (7, 'Yearly Physical', 'UF Health North', 'Just a yearly checkup',
     TIMESTAMP '2026-10-25 13:43:41.373', TIMESTAMP '2025-10-25 14:43:41.373'),

    (7, 'Routine Bloodwork', 'Baptist Health', 'Full blood panel',
     TIMESTAMP '2026-10-27 14:00:00', TIMESTAMP '2026-10-27 15:00:00'),

    (7, 'Teeth Cleaning', 'Oceanway Dental', 'Routine teeth cleaning',
     TIMESTAMP '2026-11-01 09:00:00', TIMESTAMP '2026-11-01 10:00:00');
