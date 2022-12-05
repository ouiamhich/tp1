import java.util.HashMap;
import java.util.Map;


public class Huffman {

    // Les arbres binaires pour representer les codes...

    static abstract class Arbre {
	void affiche() {
	    afficheCode("");
	}
	abstract void afficheCode(String s);
	abstract void CompleterRacine(Arbre r);
	abstract String Decoder(String m,int i);
    }
    
    // class noeud qui va hérite de classe arbre
    
    static class Noeud extends Arbre {
    	public Arbre fg, fd;
    	Noeud(Arbre g, Arbre d) {fg=g; fd=d;}
    	void afficheCode(String s) {
    	    fg.afficheCode(s+"0");
    	    fd.afficheCode(s+"1");
    	}
    	void CompleterRacine(Arbre r) {
    	    fg.CompleterRacine(r); 
    	    fd.CompleterRacine(r); 
    	}
    	String Decoder(String m,int i) {
    	    if (i>= m.length()) return "";
    	    if (m.charAt(i) == '0') 
    		return fg.Decoder(m,i+1);
    	    else return fd.Decoder(m,i+1);
    	}

     }
    
    // Paire arbre,freq
    static class PaireFA {
		public int freq;
		public Arbre a;
		PaireFA(int f, Arbre arb) {freq=f;a=arb;}
    }
    
    // Liste chainée triée selon freq
    static class LCT {
    	//cellule de la liste
		static class Cell {
		    PaireFA paire;
		    Cell suiv;
		    Cell(int f, Arbre arb,Cell s) {paire = new PaireFA(f,arb); suiv = s;}
		}
	    
		Cell premier;
		int taille;
	
		LCT() {premier = null; taille=0;}
		//ajouter en respectant le tri de la liste 
		void Ajouter(Arbre arb, int f) {
		    taille++;
		    if (premier == null) {
		    	premier = new Cell(f,arb,null);
		    }
		    else if (premier.paire.freq >= f) {
				premier = new Cell(f,arb,premier);
				return;
		    }
		    else { 
				Cell prec=null, aux = premier;
				while (aux != null && aux.paire.freq < f) {
				    prec = aux;
				    aux = aux.suiv;
			}
				prec.suiv = new Cell(f,arb,aux);
		    }
		}
		
	//extraire la case de minimum frequence
		PaireFA ExtraireMin() {
		    if (premier==null) {
				System.out.println("ExtraireMin : erreur ! Liste vide !");
				return new PaireFA(0,null);
		    }
		    taille--;
		    PaireFA p = premier.paire;
		    premier = premier.suiv;
		    return p;
		}
    }
    
    // Add Decoder
    static String Decoder(String m,Arbre code) {
    	return code.Decoder(m,0);
    }


	static Arbre CodeH(HashMap<Character, Integer> HashMap) {
		LCT liste = new LCT();
		System.out.println("Construction des feuilles... et insertion dans la liste... ");
		for (Map.Entry entry : HashMap.entrySet()) 
			liste.Ajouter(new Feuille((char) entry.getKey()),(int) entry.getValue());
		
		System.out.println("Construction du code... ");
		PaireFA p1, p2;
		while (liste.taille > 1) {
			p1 = liste.ExtraireMin();
			p2 = liste.ExtraireMin();
			liste.Ajouter(new Noeud(p1.a,p2.a), p1.freq+p2.freq);
		}
		p1 = liste.ExtraireMin();
		// on finalise le code et faisant pointer les feuilles à la racine...
		Arbre code = p1.a; 
		code.CompleterRacine(code);
		
		return code;
		}
	

	// La classe Feuille ( herite de la classe Arbre )
	static class Feuille extends Arbre {
		public char lettre;
		public Arbre racine;

		Feuille(char c) {
			lettre=c;racine=null;
		}

		void afficheCode(String s) {
	    	System.out.println(lettre+" -> "+s);
		}

		void CompleterRacine(Arbre r) {
			racine = r; 
		}

		String Decoder(String m,int i) {
	    	return lettre+racine.Decoder(m,i);
		}
		
    }
	
	static HashMap<Character, Integer> characterCount(String inputString)
    {
        HashMap<Character, Integer> charCountMap
            = new HashMap<Character, Integer>();
         char[] strArray = inputString.toCharArray();
         
         // checking each char of strArray
        for (char c : strArray) {
            if (charCountMap.containsKey(c)) {
 
                // If char is present in charCountMap,
                // incrementing it's count by 1
                charCountMap.put(c, charCountMap.get(c) + 1);
            }
            else {
 
                // If char is not present in charCountMap,
                // putting this char to charCountMap with 1 as it's value
                charCountMap.put(c, 1);
            }
        }

        return charCountMap;
    }
    
    public static void main(String[] args) {
    	
    	String str= "Huffman";
    	
    	HashMap<Character, Integer> CharOcc = characterCount(str);

		
		//codage
		Arbre Code = CodeH(CharOcc);
		Code.affiche();
		
		//decodage
		System.out.println("Decodage...");
		System.out.println("010011111110100100");
		System.out.println(Decoder("010011111110100100",Code));

	}


}
