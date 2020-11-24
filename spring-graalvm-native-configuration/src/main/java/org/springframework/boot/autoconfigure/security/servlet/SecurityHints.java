/*
 * Copyright 2020 the original author or authors.
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
package org.springframework.boot.autoconfigure.security.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.nativex.extension.NativeImageHint;
import org.springframework.nativex.extension.NativeImageConfiguration;
import org.springframework.nativex.extension.TypeInfo;
import org.springframework.nativex.type.AccessBits;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProviderNotFoundEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProxyUntrustedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

@NativeImageHint(trigger=SecurityAutoConfiguration.class,typeInfos= {
		@TypeInfo(
				// This one is interesting. This type is the return value of an @Bean method but needs DECLARED_METHODS
				// which the default @Bean processing doesn't currently include (because not all @Bean methods need it
				// and if you add it for all the memory jump is a little annoying - 3M on gs-securing-web)
				typeNames = "org.springframework.security.config.annotation.web.configuration.AutowiredWebSecurityConfigurersIgnoreParents",
				types= {SecurityExpressionOperations.class,SecurityExpressionRoot.class,WebSecurityExpressionRoot.class},
				access=AccessBits.CLASS|AccessBits.DECLARED_METHODS|AccessBits.DECLARED_FIELDS),
		@TypeInfo(types= {
				// From DefaultAuthenticationEventPublisher
				BadCredentialsException.class,AuthenticationFailureBadCredentialsEvent.class,
				UsernameNotFoundException.class,
				AccountExpiredException.class,AuthenticationFailureExpiredEvent.class,
				ProviderNotFoundException.class,AuthenticationFailureProviderNotFoundEvent.class,
				DisabledException.class,AuthenticationFailureDisabledEvent.class,
				LockedException.class,AuthenticationFailureLockedEvent.class,
				AuthenticationServiceException.class,AuthenticationFailureServiceExceptionEvent.class,
				CredentialsExpiredException.class,AuthenticationFailureCredentialsExpiredEvent.class,
				AuthenticationFailureProxyUntrustedEvent.class,
				
				// See comment below about RestrictedRequestWrapper

				},
				typeNames= {
						"org.springframework.security.authentication.cas.ProxyUntrustedException",
				}
		),
		// TODO interesting that gs-securing-web causes these to be needed although it is in thymeleaf (due to SpEL expressions I think)
		@TypeInfo(
			typeNames = "org.thymeleaf.standard.expression.RestrictedRequestAccessUtils$RestrictedRequestWrapper",
			types= { HttpServletRequestWrapper.class,ServletRequestWrapper.class,ServletRequest.class},
			access=AccessBits.CLASS|AccessBits.DECLARED_CONSTRUCTORS|AccessBits.DECLARED_FIELDS|AccessBits.DECLARED_METHODS),

		@TypeInfo(typeNames = {
				"org.springframework.boot.autoconfigure.security.DefaultWebSecurityCondition",
				"org.springframework.boot.autoconfigure.security.DefaultWebSecurityCondition$Classes",
				"org.springframework.boot.autoconfigure.security.DefaultWebSecurityCondition$Beans",
		}, access = AccessBits.ALL),
		@TypeInfo(types= BasicErrorController.class, access=AccessBits.LOAD_AND_CONSTRUCT_AND_PUBLIC_METHODS)
})
public class SecurityHints implements NativeImageConfiguration {
}
