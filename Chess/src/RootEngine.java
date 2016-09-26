import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
	//nouveau bug: suivant comment, la variable case_controllee n'est pas mise à jour!
public class RootEngine {
	int NcoupureDelta = 0;
	int NTranspo = 0;
	int NTranspoBorn = 0;
	int NDetection = 0;
	int Nexist = 0;
	int NABP = 0;
	int NPutTranspoTab = 0;
	int NDelTranspoTab = 0;
	int Futility;
	int dMax;
	static int[][] Z = genZ(); //Z est unique et invariable, c'est plus pratique de le mettre static pour y accéder facilement depuis n'importe quelle position
//	int NumeroCoup = 0; //le numero du coup de la partie, on joue en premier le coup 0
	/////////////Ce que contient notre RootEngine (unique pour toute une partie!!!)
	
	ArrayList<Integer> currentV; //on va utiliser le hachage des positions. Sert à détecter les répétitions de coups
 //	int[][] KillerMoves = new int[50][2]; //faut voir si on utilise vraiment!
	ArbreVariante Tree; //l'arbre de variante
	int Nnoeuds = 0;
 //on va créer un algo pour le trouver
	Hashtable<Integer, Integer[]> TranspoTable;
	Hashtable<Integer, Integer[]> UpperBoundTable; //upperBound: limite supérieure
	Hashtable<Integer, Integer[]> LowerBoundTable; //noeuds dont on connaît la limite inférieure
	int nbRepet = 0;
	
	
	public RootEngine()
	{
		this.TranspoTable = new Hashtable<Integer, Integer[]>();
		this.UpperBoundTable = new Hashtable<Integer, Integer[]>();
		this.LowerBoundTable = new Hashtable<Integer, Integer[]>();
		this.currentV = new ArrayList<Integer>();
	}
	
	
	
	public int alphabeta(Position p, int d, int a, int b)    ///d est la profondeur AVANT LA FIN: on considère que les coups légaux de p sont trouvés
	{			
		
		////////a = -Infini, b = +Infini au premier appel de la fonction
		this.Nnoeuds++;

		int Best;
		Integer[] BoundTab = null;
		Integer[] TranspoTab = null;
		boolean Exists = false; //deviendra vrai si la position existe dans la table de transpo
		boolean Exists2= false; //deviendra vrai si la pos. existe dans une table de transpo bornée
		int h = p.hashCode();
		int MeilleurCoup = 0; //le meilleur coup, pas son numéro!
		//je décide de regarder en tout premier si cette position est déjà survenue dans la variante. 
		//doit être fait avant d'appeler l'alphabeta des prises, car il ne test pas les répets (ça n'est utile que pour la position de base,
		//les prises empêchant les répet)
		if(currentV.contains(h))
		{
			
			this.nbRepet++;
	
			return 0; //en résumé: si la position existe dans la variante, alors c'est nulle (0)
		} 
		
		//ensuite, on ajoute h à la currentV, il ne faut pas le faire avant car sinon il va détecter des répét pour rien
		this.currentV.add(h);
		
		///test des transpositions
		if(TranspoTable.containsKey(h))
		{
			Exists = true;
			TranspoTab = TranspoTable.get(h);
			if(TranspoTab[1] >= d)
			{
				this.NTranspo++;
				int score = TranspoTab[0];
				this.currentV.remove(this.currentV.size()-1);
				return score;
			}
			else
			{
				MeilleurCoup = TranspoTab[2];
			}
			
		}

		if(p.trait) //test de transpo dans la LowerBoundTable
		{
			if(LowerBoundTable.containsKey(h))
			{
				Exists2 = true;
				Nexist++;
				BoundTab = LowerBoundTable.get(h);
				if(BoundTab[1] >= d)
				{
					Best = BoundTab[0];  //Best devient le minimum que les blancs peuvent obtenir, on peut directement tenter une coupure beta
					
					if(Best>b)
					{
						this.NTranspoBorn++;
						this.currentV.remove(this.currentV.size()-1);
						return Best;
					} //coupure beta
					if(a<Best){a=Best;}//pourra servir aux élagages de futilité, éventuellement
				}
				else
				{
					Best = -100000;
				}
			}
			else
			{
				Best = -100000;
			}
		}
		
		else //test de transpo dans la UpperBoundTable
		{
			if(UpperBoundTable.containsKey(h))
			{
				Nexist++;
				Exists2 = true;
				BoundTab = UpperBoundTable.get(h);
				if(BoundTab[1] >= d)
				{
					Best = BoundTab[0];
					
					if(a>Best) //tentative de coupure alpha
					{
						this.NTranspoBorn++;
						this.currentV.remove(this.currentV.size()-1);
						return Best;
					}
					if(Best<b){b=Best;} //on met à jour b
				}
				else
				{
					Best = 100000;
				}
			}
			else
			{
				Best = 100000;
			}
		}
		
		if(d == 0) 		///si d = 0, alors on est au bout de la variante, étant donné que d est initialisé à la profondeur Max et se décrémente à chaque appel d'AlphaBeta
		{				// A partir de là, on fait un alphabeta des prises. On part du principe que dMax de l'arbre est plus petit que la profondeur actuelle
 			int score = AlphaBetaDesPrises(p, a, b);
 			NABP++;
 			this.currentV.remove(this.currentV.size()-1);
 			Integer[] tab = {score, 0, 0}; //l'ABP n'est pas censé trouver le meilleur coup!
 			TranspoTable.put(h, tab);
 			return score;
		}
		else
		{ 
			ArrayList<Position> posf = p.coups_legaux();
			
			
			if(posf.isEmpty())  //MAT OU PAT. Pour que l'ordinateur ait envie de mater vite, il faut donner une plus grande valeur si le mat est rapide. 
				//Le mat est rapide s'il est détecté dans un noeud à une profondeur lointaine de la fin, donc si d est grand.
			{
				if(p.SimulerCoup(0).Legal()) //si c'est légal de passer, c'est PAT
				{
	
					this.currentV.remove(this.currentV.size()-1);
					return 0;
	
				}
				else
				{
					this.currentV.remove(this.currentV.size()-1);
					if(p.trait){return -100000-d;}
					else{return 100000+d;}
	/* d est utilisé pour ajuter l'éval du mat, pour améliorer 
	 * les mats rapides. Comme c'est la profondeur avant la fin, 
	 * plus elle est grande, plus on est proche du début,
	 * donc plus le mat est rapide. Mais ceci n'est valable que pour
	 * chaque itération de l'iterativeDeepening séparément, de toute 
	 * façon, les informations du meilleur coup d'une itération de 
	 * l'iterativeDeepening en cours n'est pas transmise et jouée 
	 * sur l'échiquier si l'itération n'est pas finie.*/
				}


			} 
				
			
			
			if(MeilleurCoup != 0)
			{
				if(p.liste_coups_legaux.contains(MeilleurCoup)){
					int i = p.liste_coups_legaux.indexOf(MeilleurCoup); //dans ces lignes, on met le meilleur coup d'une analyse antérieure en premier dans les tableaux coups_legaux et posf
					Position MeilleureFille = posf.get(i);
					posf.remove(i); posf.add(0, MeilleureFille);
					p.liste_coups_legaux.remove(i);
					p.liste_coups_legaux.add(0, MeilleurCoup);
				}
			}
			

			
			int Val = 0;
			int NumeroPosF = 0;
			int NumeroMeilleurCoup = 0;
			if(p.trait) ////Si c'est aux blancs, alors on a affaire à un noeud MAX
			{				
				
				for(Position pf: posf)
				{
					
					if(d == 1 && Exists) //si nous sommes à 1 noeud de la fin et que la position existe dans la table de transpo (et par conséquent nous avons l'évaluation)
					{
						if(TranspoTab[0]+150+p.deltaMatos(p.liste_coups_legaux.get(NumeroPosF)) < a){NumeroPosF++; Futility++; continue;}
					}
					
					Val = alphabeta(pf, d-1, a, b);					//à la fin du alpha-beta, ce coup sera supprimé automatiquement
					
					if(Val > Best)
					{
						Best = Val;
						NumeroMeilleurCoup = NumeroPosF;
					
						if(Best > b) //ATTENTION, ne pas mettre >=!
						{
							this.currentV.remove(this.currentV.size()-1);
							
												
							if(!Exists2) //alors on ajoute l'information
							{
								Integer[] tab = {Best, d};
								LowerBoundTable.put(h, tab);
								this.NPutTranspoTab++;
							}
							else if(BoundTab[1] < d || (BoundTab[1] == d && Best > BoundTab[0])) //cela signifie que si on a une info plus précise au niveau de la profondeur, ou de l'eval min, on remplace
							{
								LowerBoundTable.remove(h);
								this.NDelTranspoTab++;
								Integer[] tab = {Best, d};
								LowerBoundTable.put(h, tab);
								this.NPutTranspoTab++;
							}
							
						
							
							return Best;
						} //coupure BETA, on peut la produire ici car elle ne se produit que si on trouve un BON coup blanc (noeud Max)
												
						
					}	
					if(a < Val) {a = Val;} //mise à jour de a
					NumeroPosF++;
				}
			
			}
			else
			{
				for(Position pf: posf)
				{
					if(d == 1 && Exists)//tentative d'élagage de futilité
					{
						if(TranspoTab[0]-150+p.deltaMatos(p.liste_coups_legaux.get(NumeroPosF)) > b){NumeroPosF++; Futility++; continue;}
					}
					
					
					Val = alphabeta(pf, d-1, a, b);
										

					if(Val < Best)
					{
			
						Best = Val;
						NumeroMeilleurCoup = NumeroPosF;
						
						if(a > Best) //ATTENTION, ne pas mettre >=!
						{
							this.currentV.remove(this.currentV.size()-1);				
					
							
							if(!Exists2) //rappel: Exists2 est le booléen qui vaut VRAI si la position est détectée dans la table UpperBound ou LowerBound
							{
								Integer[] tab = {Best, d};
								UpperBoundTable.put(h, tab);
								this.NPutTranspoTab++;
							}
							else if(BoundTab[1] < d || (BoundTab[1] == d && Best < BoundTab[0])) //cela signifie qu'on a une info plus précise au niveau de la profondeur, ou de l'eval max, on remplace
							{
								UpperBoundTable.remove(h);
								this.NDelTranspoTab++;
								Integer[] tab = {Best, d};
								UpperBoundTable.put(h, tab);
								this.NPutTranspoTab++;
							}
						
							
							
							return Best;
						} //coupure alpha. C'est ici, avant le return que pourraient être stockées les positions bornées.
					}
					if(b > Val) {b = Val;}
					NumeroPosF++;
				}
			}
	
			this.currentV.remove(this.currentV.size()-1);			
			Integer[] tab = {Best, d, p.liste_coups_legaux.get(NumeroMeilleurCoup)};
			
			if(Exists) ///si on arrive à ce stade du programme, ça veut dire que notre tableau peut être amélioré à l'indice h
			{
				this.TranspoTable.remove(h);
			}
			
			this.TranspoTable.put(h, tab);
			
			if(Exists2)
			{
				if(BoundTab[1] <= tab[1]) 
				{
					this.NDelTranspoTab++;
					if(p.trait) {LowerBoundTable.remove(h);} else {UpperBoundTable.remove(h);}
				}
			}
			
			return Best;
		}
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public int AlphaBetaDesPrises(Position p, int a, int b)  
	{
		ArrayList<Position> posf = p.coups_legaux();
		if(posf.isEmpty())
		{
			if(p.SimulerCoup(0).Legal()) //si c'est légal de passer, c'est PAT
			{
				return 0;
			}
			else
			{
				if(p.trait){return -100000;}
				else{return 100000;}
			}
		}
		
	
		ArrayList<Integer> prises = new ArrayList<Integer>();///les coups de prises, on ne modifie pas p.coups_legaux car c'est utilisé dans l'éval 
		int j = 0;
		for(int i = 0; i<p.liste_coups_legaux.size();i++) 
		{
			if(p.deltaMatos(p.liste_coups_legaux.get(i)) > 100 || p.deltaMatos(p.liste_coups_legaux.get(i)) < -100) 
			{
				prises.add(p.liste_coups_legaux.get(i));
				
				j++;
			}
			else
			{
				posf.remove(j); //comme on supprime un élement, le nouvel élement analysé sera à nouveau à l'indice j
			}

		}

		if(prises.isEmpty())   //si il n'y a pas de position filles, alors la position est stable.
		{
			return Eval.Evaluation(p);
		}
		
		else
		{
			
			
			int Val;
			int pat_score = Eval.Evaluation(p); //on part du principe, dans l'AlphaBeta des prises, que si il est mieux de ne pas s'engager dans les prises,  
										   //alors on ne s'y engage pas, on retourne l'éval
			if(p.trait && pat_score > b) {return pat_score;}
			if(!p.trait && a > pat_score){return pat_score;} 
			int n = 0;
			int Best = pat_score;
			int i2;
			
			if(p.trait)
			{
				//tri de posf, tri par insertion utilisé (comme posf est petit). On trie en fonction de la taille de la pièce adverse prise, puis de la taille de notre pièce en cas d'égalité
				for(int i = 1; i<posf.size() ;i++) 
				{
					int coup = prises.get(i);
					Position copie = posf.get(i);
					for(i2 = i; i2>0 && p.deltaMatos(prises.get(i2-1)%100) < p.deltaMatos(coup); i2--)
					{
						prises.set(i2, prises.get(i2-1));
						posf.set(i2, posf.get(i2-1));
					}
					prises.set(i2, coup);
					posf.set(i2, copie);
				}
				

				
						
				for(Position pf: posf)
				{
					if(pat_score+150+p.deltaMatos(prises.get(n)) < a){n++; this.NcoupureDelta++; continue;} //coupure delta
					
					Val = AlphaBetaDesPrises(pf, a, b);	//à la fin du alpha-beta, ce coup sera supprimé automatiquement
					if(Val > Best)
					{
						Best = Val;
					
					
						if(Best > b) //coupure beta //ATTENTION, ne pas mettre >=!!
						{
							return Best;
						} //coupure BETA, on peut la produire ici car elle ne se produit que si on trouve un BON coup blanc (noeud Max)
													
					}	
					if(a < Val) {a = Val;} //mise à jour de a
					n++;
				}
			}
			
			else
			{
				
				//tri de posf, tri par insertion utilisé (comme posf est petit). On trie en fonction de la taille de la pièce adverse prise, puis de la taille de notre pièce en cas d'égalité
				for(int i = 1; i<posf.size() ;i++) 
				{
					int coup = prises.get(i);
					Position copie = posf.get(i);
					for(i2 = i; i2>0 &&p.deltaMatos(prises.get(i2-1)%100) > p.deltaMatos(coup); i2--)
					{
						prises.set(i2, prises.get(i2-1));
						posf.set(i2, posf.get(i2-1));
					}
					prises.set(i2, coup);
					posf.set(i2, copie);
				}
				
				
				for(Position pf: posf)
				{
					
					if(pat_score-200+p.deltaMatos(prises.get(n)) > b){n++; this.NcoupureDelta++; continue;} //coupure delta, mais on oublie les promotions en procédant ainsi!
					
					
					Val = AlphaBetaDesPrises(pf, a, b);
										

					if(Val < Best)
					{
			
						Best = Val;
						
						if(a > Best) //coupure alpha ATTENTION: ne pas mettre >=!
						{
							//this.currentV.remove(dMax-d);                      A VOIR, mais je pense que l'ABP ne touchera pas à CurrentV					
							return Best;
						} 
					}
					if(b > Val) {b = Val;}
					n++;
				}
			}
			return Best;							
		}
	}
	
	
	
	
	////////////////////////////////////APPROFONDISSEMENT ITERATIF///////////////////////////////////////////////////////////
	
	public int IterativeDeepening(Position p, long TMAX)  ///est fait pour être utilisé avec des positions de bases. Les coups légaux de p sont trouvés dans l'alphabeta
	{			
		NcoupureDelta = 0;
		NTranspo = 0;
		NTranspoBorn = 0;
		NDetection = 0;
		nbRepet = 0;
		Nnoeuds = 0;
		Eval.i=0;
		Position.N=0;
		NABP = 0;
		///TMAX en ns
		int d = 1; //la position de base est en profondeur zéro, mais la première recherche est en profondeur 1. 

		long T0 = System.nanoTime();
		int val = 0;

		while(System.nanoTime()-T0<TMAX*1000000000)
		{								
			this.dMax = d;
			val = alphabeta(p, d, -100000, 100000);
			

			if((p.trait && val >= 10000) || (!p.trait && val <= -10000)){return val;}
			System.out.print("Meilleur Coup: ");
			try
			{
				System.out.println(TranspoTable.get(p.hashCode())[2]);
			}
			catch(NullPointerException e)
			{
				System.out.println("0");
			}
			System.out.println();
			System.out.print("d = ");System.out.println(d);
			System.out.print("Eval = ");System.out.println(val);
			System.out.println("****************************************************");

			d++;
		}
		System.out.print("N eval: ");
		System.out.println(Eval.i);
		System.out.print("N coups légaux:");
		System.out.println(Position.N);
		System.out.print("N Transpo: ");
		System.out.println(this.NTranspo);
		System.out.print("N Transpo Bornées: ");
		System.out.println(this.NTranspoBorn);
		System.out.print("N Repet: ");
		System.out.println(this.nbRepet);
		System.out.print("N Coupures delta:");
		System.out.println(this.NcoupureDelta);
		System.out.print("N ABP:");
		System.out.println(NABP);
		System.out.print("Nexist dans les BoundTables: ");
		System.out.println(Nexist);
		System.out.print("N élagage futilité: ");
		System.out.println(Futility);
		System.out.print("N Put dans les BoundTables: ");
		System.out.println(NPutTranspoTab);
		System.out.print("N Del dans les BoundTables");
		System.out.println(this.NDelTranspoTab);
		
		return val;
	}
	

	static public int[][] genZ()
	{
		int[][] Zo = new int[64][19];
		
		//initialisation matrice Z pour hachage. Elle est trop longue, mais unique dans tout le programme, ça n'a donc pas trop d'importance (trouver une clé dans ce tableau se fait de manière constante)
		int i=0;
		Random r = new Random();
		while(i<64)
		{
			int j=0;
			while(j<19)
			{
				Zo[i][j] = (r.nextInt(2000000000)+100000000); //L'indice int[x][2], vide, est utilisé pour les prises en passants, un XOR sera si la case X est une PepWeakSquare.
				j++;										  //Les indices int[0][3], int[1][3], int[2][3] et int[3][3] auront un XOR appliqué GroqueB, ProqueB, GroqueN, ProqueN est possible.
															  //Le trait: si true, l'indice [4] [3] sera utilisé.
			}
			i++;
		}
		return Zo;
	}
	
}





/////////////////////////////////////////////////////