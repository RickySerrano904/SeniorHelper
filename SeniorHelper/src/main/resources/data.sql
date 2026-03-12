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
    ('Quiz - Spotting Fake Messages',        1),
    ('Quiz - Passwords & Privacy',           2),
    ('Quiz - Device Defense',                3),
    ('Quiz - Safe Shopping Online',          4),
    ('Quiz - Imposter Scams',                5),
    ('Quiz - What to Do If Scammed',         6);

-- =========================
-- Questions
-- =========================

-- Module 1
INSERT INTO questions (text, quiz_id) VALUES
                                          ('Which of these is a common sign that a message is fake?', 1),
                                          ('You get an unexpected text saying your bank account will be closed today. What should you do?', 1),
                                          ('What is the safest thing to do when you receive a suspicious message?', 1);

-- Module 2
INSERT INTO questions (text, quiz_id) VALUES
                                          ('Which of these is the strongest password?', 2),
                                          ('Why is it risky to use the same password for multiple accounts?', 2),
                                          ('Where is it safest to store your passwords?', 2);

-- Module 3
INSERT INTO questions (text, quiz_id) VALUES
                                          ('Why are software updates important for your security?', 3),
                                          ('What is the benefit of turning on automatic updates?', 3),
                                          ('What happens if you keep using outdated software?', 3);

-- Module 4
INSERT INTO questions (text, quiz_id) VALUES
                                          ('What should you look for in the browser address bar before entering payment details?', 4),
                                          ('Which payment method gives you the best fraud protection when shopping online?', 4),
                                          ('A new website is selling brand-name products at 90% off. What should you do?', 4);

-- Module 5
INSERT INTO questions (text, quiz_id) VALUES
                                          ('A caller says they are from the IRS and demands immediate payment. What is this?', 5),
                                          ('What is the safest way to verify if a caller is really from your bank?', 5),
                                          ('Which of these is a warning sign of an imposter scam?', 5);

-- Module 6
INSERT INTO questions (text, quiz_id) VALUES
                                          ('You just realized you were scammed. What should you do first?', 6),
                                          ('Who should you contact if you sent money to a scammer?', 6),
                                          ('Where can you report a scam to help protect others?', 6);

-- =========================
-- Answers
-- =========================

-- Module 1, Q1
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. It uses your full name and looks official',                 FALSE, (SELECT question_id FROM questions WHERE text = 'Which of these is a common sign that a message is fake?' AND quiz_id = 1)),
                                                     ('B. It creates urgency and asks you to act immediately',        TRUE,  (SELECT question_id FROM questions WHERE text = 'Which of these is a common sign that a message is fake?' AND quiz_id = 1)),
                                                     ('C. It comes from a phone number you recognize',               FALSE, (SELECT question_id FROM questions WHERE text = 'Which of these is a common sign that a message is fake?' AND quiz_id = 1));

-- Module 1, Q2
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. Click the link in the message to check your account',      FALSE, (SELECT question_id FROM questions WHERE text = 'You get an unexpected text saying your bank account will be closed today. What should you do?' AND quiz_id = 1)),
                                                     ('B. Reply to the message asking for more details',             FALSE, (SELECT question_id FROM questions WHERE text = 'You get an unexpected text saying your bank account will be closed today. What should you do?' AND quiz_id = 1)),
                                                     ('C. Call your bank directly using the number on their website',TRUE,  (SELECT question_id FROM questions WHERE text = 'You get an unexpected text saying your bank account will be closed today. What should you do?' AND quiz_id = 1));

-- Module 1, Q3
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. Reply to ask if the message is real',                      FALSE, (SELECT question_id FROM questions WHERE text = 'What is the safest thing to do when you receive a suspicious message?' AND quiz_id = 1)),
                                                     ('B. Do not click any links — verify by contacting the company directly', TRUE, (SELECT question_id FROM questions WHERE text = 'What is the safest thing to do when you receive a suspicious message?' AND quiz_id = 1)),
                                                     ('C. Forward it to friends so they can check it for you',       FALSE, (SELECT question_id FROM questions WHERE text = 'What is the safest thing to do when you receive a suspicious message?' AND quiz_id = 1));

-- Module 2, Q1
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. john1950',                                                 FALSE, (SELECT question_id FROM questions WHERE text = 'Which of these is the strongest password?' AND quiz_id = 2)),
                                                     ('B. password123',                                              FALSE, (SELECT question_id FROM questions WHERE text = 'Which of these is the strongest password?' AND quiz_id = 2)),
                                                     ('C. River!Sunset42',                                           TRUE,  (SELECT question_id FROM questions WHERE text = 'Which of these is the strongest password?' AND quiz_id = 2));

-- Module 2, Q2
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. If one account is hacked, criminals can access your other accounts too', TRUE, (SELECT question_id FROM questions WHERE text = 'Why is it risky to use the same password for multiple accounts?' AND quiz_id = 2)),
                                                     ('B. It makes it harder to remember your passwords',            FALSE, (SELECT question_id FROM questions WHERE text = 'Why is it risky to use the same password for multiple accounts?' AND quiz_id = 2)),
                                                     ('C. It slows down your internet connection',                   FALSE, (SELECT question_id FROM questions WHERE text = 'Why is it risky to use the same password for multiple accounts?' AND quiz_id = 2));

-- Module 2, Q3
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. In a text message or email to yourself',                   FALSE, (SELECT question_id FROM questions WHERE text = 'Where is it safest to store your passwords?' AND quiz_id = 2)),
                                                     ('B. In a notebook kept in a secure place at home, or in a password manager', TRUE, (SELECT question_id FROM questions WHERE text = 'Where is it safest to store your passwords?' AND quiz_id = 2)),
                                                     ('C. In a sticky note on your computer screen',                 FALSE, (SELECT question_id FROM questions WHERE text = 'Where is it safest to store your passwords?' AND quiz_id = 2));

-- Module 3, Q1
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. They add new games and entertainment features',            FALSE, (SELECT question_id FROM questions WHERE text = 'Why are software updates important for your security?' AND quiz_id = 3)),
                                                     ('B. They fix security weaknesses that hackers can exploit',    TRUE,  (SELECT question_id FROM questions WHERE text = 'Why are software updates important for your security?' AND quiz_id = 3)),
                                                     ('C. They make your device look more modern',                   FALSE, (SELECT question_id FROM questions WHERE text = 'Why are software updates important for your security?' AND quiz_id = 3));

-- Module 3, Q2
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. Your device updates itself without you having to remember', TRUE,  (SELECT question_id FROM questions WHERE text = 'What is the benefit of turning on automatic updates?' AND quiz_id = 3)),
                                                     ('B. It makes your device run faster immediately',              FALSE, (SELECT question_id FROM questions WHERE text = 'What is the benefit of turning on automatic updates?' AND quiz_id = 3)),
                                                     ('C. It backs up all your photos automatically',                FALSE, (SELECT question_id FROM questions WHERE text = 'What is the benefit of turning on automatic updates?' AND quiz_id = 3));

-- Module 3, Q3
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. Nothing — old software works just as safely as new software', FALSE, (SELECT question_id FROM questions WHERE text = 'What happens if you keep using outdated software?' AND quiz_id = 3)),
                                                     ('B. Your device becomes a known target for hackers',           TRUE,  (SELECT question_id FROM questions WHERE text = 'What happens if you keep using outdated software?' AND quiz_id = 3)),
                                                     ('C. Your device will stop connecting to the internet',         FALSE, (SELECT question_id FROM questions WHERE text = 'What happens if you keep using outdated software?' AND quiz_id = 3));

-- Module 4, Q1
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. A colorful logo and professional design',                  FALSE, (SELECT question_id FROM questions WHERE text = 'What should you look for in the browser address bar before entering payment details?' AND quiz_id = 4)),
                                                     ('B. A padlock icon and an address starting with https://',     TRUE,  (SELECT question_id FROM questions WHERE text = 'What should you look for in the browser address bar before entering payment details?' AND quiz_id = 4)),
                                                     ('C. A pop-up saying the site is verified',                     FALSE, (SELECT question_id FROM questions WHERE text = 'What should you look for in the browser address bar before entering payment details?' AND quiz_id = 4));

-- Module 4, Q2
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. Debit card, because it comes straight from your bank',     FALSE, (SELECT question_id FROM questions WHERE text = 'Which payment method gives you the best fraud protection when shopping online?' AND quiz_id = 4)),
                                                     ('B. Wire transfer, because it is fast',                        FALSE, (SELECT question_id FROM questions WHERE text = 'Which payment method gives you the best fraud protection when shopping online?' AND quiz_id = 4)),
                                                     ('C. Credit card or PayPal, because charges can be disputed',   TRUE,  (SELECT question_id FROM questions WHERE text = 'Which payment method gives you the best fraud protection when shopping online?' AND quiz_id = 4));

-- Module 4, Q3
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. Buy quickly before the deal expires',                      FALSE, (SELECT question_id FROM questions WHERE text = 'A new website is selling brand-name products at 90% off. What should you do?' AND quiz_id = 4)),
                                                     ('B. Search for reviews of the site before making any purchase', TRUE,  (SELECT question_id FROM questions WHERE text = 'A new website is selling brand-name products at 90% off. What should you do?' AND quiz_id = 4)),
                                                     ('C. It must be safe if it accepts credit cards',               FALSE, (SELECT question_id FROM questions WHERE text = 'A new website is selling brand-name products at 90% off. What should you do?' AND quiz_id = 4));

-- Module 5, Q1
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. A legitimate IRS collections process',                     FALSE, (SELECT question_id FROM questions WHERE text = 'A caller says they are from the IRS and demands immediate payment. What is this?' AND quiz_id = 5)),
                                                     ('B. An imposter scam — the IRS never calls demanding immediate payment', TRUE, (SELECT question_id FROM questions WHERE text = 'A caller says they are from the IRS and demands immediate payment. What is this?' AND quiz_id = 5)),
                                                     ('C. A routine audit notification',                             FALSE, (SELECT question_id FROM questions WHERE text = 'A caller says they are from the IRS and demands immediate payment. What is this?' AND quiz_id = 5));

-- Module 5, Q2
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. Ask the caller for their employee ID number',              FALSE, (SELECT question_id FROM questions WHERE text = 'What is the safest way to verify if a caller is really from your bank?' AND quiz_id = 5)),
                                                     ('B. Hang up and call the bank back using the number on your card or their website', TRUE, (SELECT question_id FROM questions WHERE text = 'What is the safest way to verify if a caller is really from your bank?' AND quiz_id = 5)),
                                                     ('C. Trust them if they already know your account number',      FALSE, (SELECT question_id FROM questions WHERE text = 'What is the safest way to verify if a caller is really from your bank?' AND quiz_id = 5));

-- Module 5, Q3
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. The caller gives you time to verify their identity',       FALSE, (SELECT question_id FROM questions WHERE text = 'Which of these is a warning sign of an imposter scam?' AND quiz_id = 5)),
                                                     ('B. The caller says you must not hang up or call anyone else', TRUE,  (SELECT question_id FROM questions WHERE text = 'Which of these is a warning sign of an imposter scam?' AND quiz_id = 5)),
                                                     ('C. The caller sends a follow-up letter in the mail',          FALSE, (SELECT question_id FROM questions WHERE text = 'Which of these is a warning sign of an imposter scam?' AND quiz_id = 5));

-- Module 6, Q1
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. Send one final message to ask for your money back',        FALSE, (SELECT question_id FROM questions WHERE text = 'You just realized you were scammed. What should you do first?' AND quiz_id = 6)),
                                                     ('B. Stop all contact with the scammer and change your passwords immediately', TRUE, (SELECT question_id FROM questions WHERE text = 'You just realized you were scammed. What should you do first?' AND quiz_id = 6)),
                                                     ('C. Wait a few days to see if the situation resolves itself',  FALSE, (SELECT question_id FROM questions WHERE text = 'You just realized you were scammed. What should you do first?' AND quiz_id = 6));

-- Module 6, Q2
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. A neighbor or family member',                              FALSE, (SELECT question_id FROM questions WHERE text = 'Who should you contact if you sent money to a scammer?' AND quiz_id = 6)),
                                                     ('B. Your bank or credit card company as soon as possible',     TRUE,  (SELECT question_id FROM questions WHERE text = 'Who should you contact if you sent money to a scammer?' AND quiz_id = 6)),
                                                     ('C. The scammer to request a refund',                          FALSE, (SELECT question_id FROM questions WHERE text = 'Who should you contact if you sent money to a scammer?' AND quiz_id = 6));

-- Module 6, Q3
INSERT INTO answers (text, correct, question_id) VALUES
                                                     ('A. There is no place to report scams',                        FALSE, (SELECT question_id FROM questions WHERE text = 'Where can you report a scam to help protect others?' AND quiz_id = 6)),
                                                     ('B. Only to your local police department',                     FALSE, (SELECT question_id FROM questions WHERE text = 'Where can you report a scam to help protect others?' AND quiz_id = 6)),
                                                     ('C. The FTC at reportfraud.ftc.gov or by calling 1-877-382-4357', TRUE, (SELECT question_id FROM questions WHERE text = 'Where can you report a scam to help protect others?' AND quiz_id = 6));
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