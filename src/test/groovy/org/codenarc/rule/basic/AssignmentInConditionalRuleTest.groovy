/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for AssignmentInConditionalRule
 *
 * @author 'Hamlet D'Arcy'
 * @author Chris Mair
 */
class AssignmentInConditionalRuleTest extends AbstractRuleTestCase {

    private static final VIOLATION_MESSAGE = 'Assignment used as conditional value, which always results in true. Use the == operator instead'

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AssignmentInConditional'
    }

    void testConditionalsWithoutAssignments_NoViolations() {
        final SOURCE = '''
            if (value == true) {
            }
            while (value == true) {
            }
            value == true ? x : y
            value == true ?: x
        '''
        assertNoViolations(SOURCE)
    }

    void testIfStatement() {
        final SOURCE = '''
            if ((value = true)) {
                // should be ==
            }
        '''
        assertSingleViolation(SOURCE, 2, 'if ((value = true))', VIOLATION_MESSAGE)
    }

    void testExpressionWithMultipleConditions_ContainsAssignment_ThrowsException() {
        final SOURCE = '''
            while(value > 5 || (value = -1)) { }
            if ((value = 5) && ready && doSomething()) { }
            if (ready && (doSomething() || (value = 5))) { }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'while(value > 5 || (value = -1)) { }', messageText:VIOLATION_MESSAGE],
            [lineNumber:3, sourceLineText:'if ((value = 5) && ready && doSomething()) { }', messageText:VIOLATION_MESSAGE],
            [lineNumber:4, sourceLineText:'if (ready && (doSomething() || (value = 5))) { }', messageText:VIOLATION_MESSAGE])
    }

    void testWhileStatement() {
        final SOURCE = '''
            while (value = true) {
                // should be ==
            }
        '''
        assertSingleViolation(SOURCE, 2, 'while (value = true)', VIOLATION_MESSAGE)
    }

    void testExpressionContainingConditionalWithAssignment_NoViolation() {
        final SOURCE = '''
            def ready = (value = true)
        '''
        assertNoViolations(SOURCE)
    }

    void testElvis() {
        final SOURCE = '''
            (value = true) ?: x
        '''
        assertSingleViolation(SOURCE, 2, '(value = true) ?: x', VIOLATION_MESSAGE)
    }

    void testTernary() {
        final SOURCE = '''
            (value = true) ? x : y
        '''
        assertSingleViolation(SOURCE, 2, '(value = true) ? x : y', VIOLATION_MESSAGE)
    }

    protected Rule createRule() {
        new AssignmentInConditionalRule()
    }
}