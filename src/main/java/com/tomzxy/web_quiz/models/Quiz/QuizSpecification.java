package com.tomzxy.web_quiz.models.Quiz;

import com.tomzxy.web_quiz.dto.requests.filter.QuizFilterReqDTO;
import com.tomzxy.web_quiz.utils.SpecificationUtils;
import org.springframework.data.jpa.domain.Specification;

public class QuizSpecification {

    public static Specification<Quiz> filter(QuizFilterReqDTO filter) {

        Specification<Quiz> spec = Specification.where(isActive());

        spec = spec.and(SpecificationUtils.equal("subject.id", filter.getSubjectId()));
        spec = spec.and(SpecificationUtils.like("title", filter.getSearch()));
        spec = spec.and(SpecificationUtils.greaterThanOrEqual("questionCount", filter.getMinQuestions()));
        spec = spec.and(SpecificationUtils.lessThanOrEqual("questionCount", filter.getMaxQuestions()));
        spec = spec.and(SpecificationUtils.greaterThanOrEqual("duration", filter.getMinDuration()));
        spec = spec.and(SpecificationUtils.lessThanOrEqual("duration", filter.getMaxDuration()));

        return spec;
    }

    public static Specification<Quiz> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("isActive"));
    }
}
