INSERT INTO Topics (Topic_category)
VALUES
('TOPIC1'),
('TOPIC2'),
('TOPIC3'),
('TOPIC4'),
('TOPIC5');

INSERT INTO Sentences (Sentence_eng, Sentence_kor, Sentence_level)
VALUES
('Hello', '안녕하세요', 1),
('This is a short sentence.', '이것은 짧은 문장입니다.', 2),
('Learning to code is fun and rewarding.', '코딩을 배우는 것은 재미있고 보람차다.', 3),
('The quick brown fox jumps over the lazy dog.', '빠른 갈색 여우가 게으른 개를 뛰어넘다.', 1),
('With great power comes great responsibility.', '큰 힘에는 큰 책임이 따른다.', 2),
('To be or not to be, that is the question.', '사는 것이냐 죽는 것이냐, 그것이 문제로다.', 3),
('In a hole in the ground there lived a hobbit.', '땅속 구멍에 호빗이 살았다.', 1),
('It was the best of times, it was the worst of times.', '최고의 순간이자 최악의 순간이었다.', 2),
('All human beings are born free and equal in dignity and rights.', '모든 인간은 존엄과 권리에 있어 자유롭고 평등하게 태어났다.', 3),
('To infinity and beyond!', '무한대와 그 너머로!', 1),
('Time is money.', '시간은 돈이다.', 2),
('Practice makes perfect.', '연습이 완벽을 만든다.', 2),
('Knowledge is power.', '지식은 힘이다.', 3),
('The early bird catches the worm.', '일찍 일어나는 새가 벌레를 잡는다.', 1),
('Actions speak louder than words.', '행동이 말보다 더 크게 말한다.', 2),
('A picture is worth a thousand words.', '한 장의 그림이 천 마디의 말보다 낫다.', 3),
('Beauty is in the eye of the beholder.', '아름다움은 보는 이의 눈에 있다.', 1),
('Brevity is the soul of wit.', '간결함은 재치의 영혼이다.', 2),
('Fortune favors the bold.', '행운은 용감한 자를 돕는다.', 3),
('The pen is mightier than the sword.', '펜은 칼보다 강하다.', 1);

INSERT INTO Sentence_Topic (Sentence_id, Topic_id)
VALUES
(1, 1), -- 'Hello' linked to 'TOPIC1'
(2, 2), -- 'This is a short sentence.' linked to 'TOPIC2'
(3, 3), -- 'Learning to code is fun and rewarding.' linked to 'TOPIC3'
(4, 1), -- 'The quick brown fox jumps over the lazy dog.' linked to 'TOPIC1'
(5, 2), -- 'With great power comes great responsibility.' linked to 'TOPIC2'
(6, 3), -- 'To be or not to be, that is the question.' linked to 'TOPIC3'
(7, 1), -- 'In a hole in the ground there lived a hobbit.' linked to 'TOPIC1'
(8, 2), -- 'It was the best of times, it was the worst of times.' linked to 'TOPIC2'
(9, 3), -- 'All human beings are born free and equal in dignity and rights.' linked to 'TOPIC3'
(10, 1), -- 'To infinity and beyond!' linked to 'TOPIC1'
(11, 2), -- 'Time is money.' linked to 'TOPIC2'
(12, 3), -- 'Practice makes perfect.' linked to 'TOPIC3'
(13, 1), -- 'Knowledge is power.' linked to 'TOPIC1'
(14, 2), -- 'The early bird catches the worm.' linked to 'TOPIC2'
(15, 3), -- 'Actions speak louder than words.' linked to 'TOPIC3'
(16, 1), -- 'A picture is worth a thousand words.' linked to 'TOPIC1'
(17, 2), -- 'Beauty is in the eye of the beholder.' linked to 'TOPIC2'
(18, 3), -- 'Brevity is the soul of wit.' linked to 'TOPIC3'
(19, 1), -- 'Fortune favors the bold.' linked to 'TOPIC1'
(20, 2); -- 'The pen is mightier than the sword.' linked to 'TOPIC2'
