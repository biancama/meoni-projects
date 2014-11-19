package com.biancama.security.prepost;

import org.springframework.security.core.Authentication;

public interface BiancamaPermission {
	boolean isAllowed(Authentication authentication, Object targetDomainObject);
}
