-- Migration: Enhance Quiz Models with Snapshot Pattern
-- Version: 2
-- Description: Add snapshot fields to quiz_attempts, add option fields to answers, drop redundant tables

-- Add new columns to answers table
ALTER TABLE answers ADD COLUMN option_order INTEGER;
ALTER TABLE answers ADD COLUMN option_label VARCHAR(10);

-- Add new columns to quiz_attempts table
ALTER TABLE quiz_attempts ADD COLUMN question_type VARCHAR(20);
ALTER TABLE quiz_attempts ADD COLUMN question_points INTEGER NOT NULL DEFAULT 1;
ALTER TABLE quiz_attempts ADD COLUMN question_level VARCHAR(20);
ALTER TABLE quiz_attempts ADD COLUMN correct_answer_text VARCHAR(1000);
ALTER TABLE quiz_attempts ADD COLUMN all_answer_options TEXT;
ALTER TABLE quiz_attempts ADD COLUMN original_question_id BIGINT;

-- Add foreign key constraint for original_question_id
ALTER TABLE quiz_attempts ADD CONSTRAINT fk_quiz_attempt_original_question 
    FOREIGN KEY (original_question_id) REFERENCES questions(id);

-- Drop redundant tables
DROP TABLE IF EXISTS option_answers;
DROP TABLE IF EXISTS answer_attempts;

-- Create indexes for better performance
CREATE INDEX idx_quiz_attempt_question_type ON quiz_attempts(question_type);
CREATE INDEX idx_quiz_attempt_question_level ON quiz_attempts(question_level);
CREATE INDEX idx_quiz_attempt_original_question ON quiz_attempts(original_question_id);
CREATE INDEX idx_answer_option_order ON answers(option_order);
CREATE INDEX idx_answer_option_label ON answers(option_label); 