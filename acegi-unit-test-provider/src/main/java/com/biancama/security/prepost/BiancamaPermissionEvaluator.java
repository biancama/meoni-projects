package com.biancama.security.prepost;

import java.io.Serializable;
import java.util.Map;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

public class BiancamaPermissionEvaluator implements PermissionEvaluator {
	
	private Map<String, BiancamaPermission> permissions;
	
	public BiancamaPermissionEvaluator(Map<String, BiancamaPermission> permissions){
		this.permissions = permissions;
	}
	@Override
	public boolean hasPermission(Authentication authentication,
			Object targetDomainObject, Object permission) {
		boolean hasPermission = false;
		if (authentication != null && targetDomainObject != null && permission instanceof String){
			try {
				hasPermission = checkPermission(authentication, targetDomainObject, (String)permission);
			} catch (PermissionNotDefinedException e) {
				e.printStackTrace();
			}
		}
		return hasPermission;
	}

	private boolean checkPermission(Authentication authentication,
			Object targetDomainObject, String permission) throws PermissionNotDefinedException {
		verifyPermissionIsDefined(permission);
		BiancamaPermission permissionVerifier = permissions.get(permission);
		return permissionVerifier.isAllowed(authentication, targetDomainObject);
	}
	private void verifyPermissionIsDefined(String permissionKey) throws PermissionNotDefinedException {
		if (!permissions.containsKey(permissionKey)){
			throw new PermissionNotDefinedException("No permission with key " + permissionKey + " defined");
		}
		
	}
	@Override
	public boolean hasPermission(Authentication authentication,
			Serializable targetId, String targetType, Object permission) {
		// TODO Auto-generated method stub
		return false;
	}

}
