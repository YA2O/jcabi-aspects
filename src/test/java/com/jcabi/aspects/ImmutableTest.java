/**
 * Copyright (c) 2012-2014, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.aspects;

import java.util.regex.Pattern;
import org.junit.Test;

/**
 * Test case for {@link Immutable} annotation and its implementation.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle ConstantUsageCheck (500 lines)
 */
@SuppressWarnings({
    "PMD.UnusedPrivateField",
    "PMD.UnusedLocalVariable",
    "PMD.FinalFieldCouldBeStatic"
})
public final class ImmutableTest {

    /**
     * Immutable can catch mutable classes.
     */
    @Test(expected = IllegalStateException.class)
    public void catchedMutableTypes() {
        new Mutable();
    }

    /**
     * ImmutabilityChecker can catch mutable classes with arrays.
     */
    @Test(expected = IllegalStateException.class)
    public void catchedMutableTypesWithArrays() {
        new MutableWithArray();
    }

    /**
     * Immutable can catch mutable classes with interfaces.
     */
    @Test(expected = IllegalStateException.class)
    public void catchedMutableTypesWithInterfaces() {
        new MutableWithInterface();
    }

    /**
     * Immutable can catch mutable classes with mutable implementation of
     * immutable interfaces.
     */
    @Test(expected = IllegalStateException.class)
    public void catchedMutableTypesWithImplementationOfImmutableInterface() {
        new MutableWithImmutableInterface();
    }

    /**
     * Immutable can pass immutable classes.
     */
    @Test
    public void passesImmutableObjects() {
        new TruelyImmutable();
    }

    /**
     * Immutable can pass immutable classes.
     */
    @Test
    public void passesImmutableObjectsWithNonPrivateFields() {
        new TruelyImmutableWithNonPrivateFields();
    }

    /**
     * Immutable can catch mutable classes with interfaces.
     */
    @Test(expected = IllegalStateException.class)
    public void catchedTypesMutableByClassInheritance() {
        new MutableByInheritance();
    }

    /**
     * Supposedly immutable class.
     */
    @Immutable
    private static final class Mutable {
        /**
         * Mutable class member.
         */
        @SuppressWarnings("PMD.ImmutableField")
        private transient String data = "hello";
    }

    /**
     * Mutable class because of array.
     */
    @Immutable
    private static final class MutableWithArray {
        /**
         * Mutable class member.
         */
        private final transient int[] data = null;
    }

    /**
     * Vague interface.
     */
    private interface MutableInterface {
    }

    /**
     * Mutable class because of mutable interface.
     */
    @Immutable
    private static final class MutableWithInterface {
        /**
         * Vague class member.
         */
        private final transient MutableInterface data = null;
    }

    /**
     * Other vague interface.
     */
    @Immutable
    private interface ImmutableInterface {
        /**
         * This function seems to be harmless.
         * @param input An input
         */
        void willBreakImmutability(int input);
    }

    /**
     * Mutable class implementing immutable interface.
     */
    @Immutable
    private static final class MutableWithImmutableInterface {
        /**
         * Supposedly immutable field that is not immutable after all.
         */
        private final ImmutableInterface impl = new ImmutableInterface() {
            private int state = 1;

            /**
             * This function breaks the immutability promised by the interface.
             */
            @Override
            public void willBreakImmutability(final int newstate) {
                this.state = newstate;
            }
        };

        /**
         * Stupid getter.
         * @return A handle to mutate the object. Not good...
         */
        public ImmutableInterface getImpl() {
            return this.impl;
        }
    }

    /**
     * Truely immutable class.
     */
    @Immutable
    private static final class TruelyImmutable {
        /**
         * Something static final.
         */
        private static final Pattern PATTERN = Pattern.compile(".?");
        /**
         * Something just static.
         */
        private static Pattern ptrn = Pattern.compile("\\d+");
        /**
         * Immutable class member.
         */
        private final transient String data = null;
        /**
         * Another immutable class member.
         */
        private final transient int number = 2;
        /**
         * Another immutable class member.
         */
        private final transient String text = "Hello, world!";
        /**
         * An immutable array member.
         */
        @Immutable.Array
        private final transient String[] texts = new String[] {"foo"};
    }

    /**
     * Truely immutable class with non-private fields.
     *
     * @checkstyle VisibilityModifier (25 lines)
     */
    @Immutable
    private static final class TruelyImmutableWithNonPrivateFields {
        /**
         * Something static final.
         */
        public static final Pattern PATTERN = Pattern.compile(".*");
        /**
         * Something just static.
         */
        public static final Pattern PTRN = Pattern.compile(".+");
        /**
         * Immutable class member.
         */
        public final String data = null;
        /**
         * Another immutable class member.
         */
        public final int number = 2;
        /**
         * Another immutable class member.
         */
        public final String text = "Hello!";
    }

    /**
     * Almost immutable class. It can be inherited, because it is non-final;
     * thus methods in the child class can return nonsensical values (e.g.
     * getters that do no return the original value of their corresponding
     * fields). Moreover, immutability cannot be forced to a subclass.
     * See <a href=
     * "http://marxsoftware.blogspot.se/2009/09/
     * is-java-immutable-class-always-final.html">
     * Is java immutable class always final?</a>
     */
    @Immutable
    private static class MutableByInheritance {
        /**
         * Immutable class member.
         */
        private final transient String data = null;

        /**
         * Could be overloaded by a child of the class and then return
         * nonsensical value.
         *
         * @return A value that could differ from what is expected if returned by an overriding method
         */
        public String getData() {
            return this.data;
        }
    }

}
