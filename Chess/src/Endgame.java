import java.util.ArrayList;


public class Endgame { //inutile!
	
	
	public static ArrayList<Integer> findPP(Boolean ColorPP, ArrayList<Integer> PP, ArrayList<Integer> PA) //optimisé pour trouver les pions passés. Utile dans certains cas
																						  //PP sont les pions passés (si il y en a), PA les pions adverses.
																						//n'est pas utilisé dans la version actuelle du programme.
	{
		ArrayList<Integer> PPC = new ArrayList<Integer>();  //les pions passés certifiés
		boolean b;
		
		//on test dès le début la couleur de PP, pour ne pas avoir à le refaire plusieurs fois.
		if(ColorPP)
		{
			for(int i:PP)
			{
				b = true;
				for(int a: PA){if((a%8 == i%8 && a>i)||(a%8-1 == i%8 && a>i+1)||(a%8+1 == i%8 && a>i)){b = false;}}  //Toutes les conditions pour qu'un pion bloque un pion passé
				if(b){for(int a: PP){if(a%8 == i%8 && a>i){b = false;}}}  //pour éviter qu'il y ait des pions doublés passés
				if(b){PPC.add(i);}
			}
			
		}
		else
		{
			for(int i:PP)
			{
				b = true;
				for(int a: PA){if((a%8 == i%8 && a<i)||(a%8-1 == i%8 && a<i)||(a%8+1 == i%8 && a+1<i)){b = false;}}  //Toutes les conditions pour qu'un pion bloque un pion passé
				if(b){for(int a: PP){if(a%8 == i%8 && a<i){b = false;}}}  //pour éviter qu'il y ait des pions doublés passés
				if(b){PPC.add(i);}
			}
		}
		return PPC;
	}
	
	//vérifie si un roi est dans le carré d'un pion (s'il est en mesure d'arrêter ce pion à lui seul) 

}
