import java.util.ArrayList;


public class Endgame { //inutile!
	
	
	public static ArrayList<Integer> findPP(Boolean ColorPP, ArrayList<Integer> PP, ArrayList<Integer> PA) //optimis� pour trouver les pions pass�s. Utile dans certains cas
																						  //PP sont les pions pass�s (si il y en a), PA les pions adverses.
																						//n'est pas utilis� dans la version actuelle du programme.
	{
		ArrayList<Integer> PPC = new ArrayList<Integer>();  //les pions pass�s certifi�s
		boolean b;
		
		//on test d�s le d�but la couleur de PP, pour ne pas avoir � le refaire plusieurs fois.
		if(ColorPP)
		{
			for(int i:PP)
			{
				b = true;
				for(int a: PA){if((a%8 == i%8 && a>i)||(a%8-1 == i%8 && a>i+1)||(a%8+1 == i%8 && a>i)){b = false;}}  //Toutes les conditions pour qu'un pion bloque un pion pass�
				if(b){for(int a: PP){if(a%8 == i%8 && a>i){b = false;}}}  //pour �viter qu'il y ait des pions doubl�s pass�s
				if(b){PPC.add(i);}
			}
			
		}
		else
		{
			for(int i:PP)
			{
				b = true;
				for(int a: PA){if((a%8 == i%8 && a<i)||(a%8-1 == i%8 && a<i)||(a%8+1 == i%8 && a+1<i)){b = false;}}  //Toutes les conditions pour qu'un pion bloque un pion pass�
				if(b){for(int a: PP){if(a%8 == i%8 && a<i){b = false;}}}  //pour �viter qu'il y ait des pions doubl�s pass�s
				if(b){PPC.add(i);}
			}
		}
		return PPC;
	}
	
	//v�rifie si un roi est dans le carr� d'un pion (s'il est en mesure d'arr�ter ce pion � lui seul) 

}
