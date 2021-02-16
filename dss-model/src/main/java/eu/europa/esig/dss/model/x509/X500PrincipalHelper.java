/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.model.x509;

import javax.security.auth.x500.X500Principal;

import eu.europa.esig.dss.enumerations.X520Attributes;

public class X500PrincipalHelper {

	private final X500Principal principal;

	public X500PrincipalHelper(X500Principal principal) {
		this.principal = principal;
	}

	public X500Principal getPrincipal() {
		return principal;
	}

	public String getCanonical() {
		return principal.getName(X500Principal.CANONICAL);
	}

	public String getRFC2253() {
		return principal.getName(X500Principal.RFC2253);
	}

	public String getPrettyPrintRFC2253() {
		return principal.getName(X500Principal.RFC2253, X520Attributes.getOidDescriptions());
	}

	public byte[] getEncoded() {
		return principal.getEncoded();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((principal == null) ? 0 : principal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		X500PrincipalHelper other = (X500PrincipalHelper) obj;
		if (principal == null) {
			if (other.principal != null) {
				return false;
			}
		} else if (!principal.equals(other.principal)) {
			return false;
		}
		return true;
	}

}
