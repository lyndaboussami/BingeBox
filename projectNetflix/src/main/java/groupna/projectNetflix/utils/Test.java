package groupna.projectNetflix.utils;

public class Test {
	public boolean testName(String ch) {
		if(ch.length()<2 ) {
			return false;
		}
		return true;
	}
	public boolean testEmail(String email) {
	    if (email == null || email.isEmpty()) {
	        return false;
	    }
	    String[] parties = email.split("@");
	    if (parties.length != 2) {
	        return false;
	    }

	    String gauche = parties[0];
	    String droite = parties[1]; 
	    if (gauche.isEmpty() || !droite.contains(".") || 
	        droite.startsWith(".") || droite.endsWith(".")) {
	        return false;
	    }

	    return true;
	}
	public boolean testPassword(String password) {
	    if (password == null || password.length() < 8) {
	        return false;
	    }

	    boolean contientLettre = false;
	    boolean contientChiffre = false;
	    boolean contientSpecial = false;
	    String caracteresSpeciaux = "!@#$%^&*()-_=+[]{}|;:,.<>?";
	    for (char c : password.toCharArray()) {
	        if (Character.isLetter(c)) {
	            contientLettre = true;
	        } else if (Character.isDigit(c)) {
	            contientChiffre = true;
	        } else if (caracteresSpeciaux.contains(String.valueOf(c))) {
	            contientSpecial = true;
	        }
	        if (contientLettre && contientChiffre && contientSpecial) {
	            break;
	        }
	    }
	    return contientLettre && contientChiffre && contientSpecial;
	}
}
