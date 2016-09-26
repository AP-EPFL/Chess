import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ArbreVariante {  //sert à gérer tout les accès aux noeuds. N'est plus utilisé!
	Position p;
	Noeud Racine;


	public ArbreVariante(Position PosBase)
	{
		this.p = PosBase;
		this.Racine = new Noeud(0);
	}	
	

	
	public ArrayList<Integer> GetBestVar()
	{
		ArrayList<Integer> BestV = new ArrayList<Integer>();
		Noeud CurrentN = this.Racine;
		while(!CurrentN.Fils.isEmpty())
		{
			CurrentN = CurrentN.Fils.get(0);
			BestV.add(CurrentN.LastMove);
		}
		return BestV;
	}
	
	public void tri()
	{
		this.Racine.TriTotal(0);
	}
	
	
	public Noeud GetNoeud(ArrayList<Integer> var)
	{
		Noeud n = this.Racine;
		for(int i:var) 
		{
			n = n.TrouverNoeudFils(i);
			if(n == null) {return null;}
		
		}
		return n;
	}
	
	public void PutNoeud(ArrayList<Integer> var, Position p)
	{
		int DernierCoup = var.get(var.size()-1);
		var.remove(var.size()-1);
		GetNoeud(var).PutNoeud(DernierCoup);
	}
	

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	public class Noeud implements Comparable<Noeud>
	{
		//Position p;
		ArrayList<Noeud> Fils;
		int LastMove;
		int Resultat;
		
		public Noeud(int LastMove) 
		{
			this.LastMove = LastMove;
			this.Fils = new ArrayList<Noeud>();
		}

		
		public void PutNoeud(int LastMove)
		{
			Fils.add(new Noeud(LastMove));
		}
		
		public Noeud TrouverNoeudFils(int coup)  //une des méthodes importantes: il faut pouvoir retrouver le noeud fils d'un noeud grâce au coup
		{
			if(coup == 0){return null;}
			for(int i = 0; i<Fils.size(); i++)
			{
				if(Fils.get(i).LastMove == coup){return Fils.get(i);}
			}
			return null;
		}
		
		
		public void TriTotal(int d) //fonction récursive triant efficacement l'arbre
		{
			if(!this.Fils.isEmpty())//si on n'est pas sur une feuille, on trie
			{
				for(Noeud n: this.Fils)
				{
					n.TriTotal(d+1);
				}
				Collections.sort(this.Fils);
				if((p.trait && d%2 == 0) || (!p.trait && d%2 == 1)) {Collections.reverse(Fils);} 
			}
			
		}	
		
	
		public void setResultat(int Resultat)
		{
			this.Resultat = Resultat;
		}		
		

		@Override
		public int compareTo(Noeud arg0) {
			
			return this.Resultat-arg0.Resultat;  ///il faut que deux noeuds soit à la même distance pour être comparés	
			///si resultat1>resultat2, alors noeud1<noeud2, comme la liste sera dans l'ordre croissant, d'où la négation
		}		
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
