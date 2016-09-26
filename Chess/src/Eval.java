import java.util.ArrayList;


public class Eval {
	
	static int i = 0;
	
	static int[] valeur_casesBlanc = {2, 4, 4, 6, 6, 4, 4, 2,  //valeur attribuée à chaque pièce blanche contrôlant cette case
	 		 2, 4, 4, 6, 6, 4, 4, 2,
	 		 4, 6, 6, 10, 10, 6, 6, 4,
	 		 6, 6, 10, 30 , 30 , 10, 6, 6,
	 		 6,10,20,40,40,20,10,6,
	 		 6, 6, 20 , 30 , 30 , 20 , 6, 6,
	 		 6, 10, 10, 20 , 20 , 10, 10, 6,
	 		 6, 6, 10, 16, 16, 10, 6, 6};

	static int[] valeur_casesNoir = {6, 6, 10, 16, 16, 10, 6, 6,  //valeur attribuée à chaque pièce noire contrôlant cette case
		   	6, 10, 10, 20 , 20 , 10, 10, 6,
		   	6, 6, 20 , 30 , 30 , 20 , 6, 6,
		   	6, 10, 20 , 40 , 40 , 20 , 10, 6,
		   	6, 6, 10, 30 , 30 , 10, 6, 6,
		   	4, 6, 6, 10, 10, 6, 6, 4,
		   	2, 4, 4, 6, 6, 4, 4, 2,
		   	2, 4, 4, 6, 6, 4, 4, 2};
	
	static int[] SafetyTableW = {					//valeur de malus pour le roi blanc s'il est sur cette case
	   0  ,0  ,10 ,20 ,20 ,10 ,0  ,0  ,
	   10 ,10 ,25 ,40 ,40 ,25 ,10 ,10 ,
	   60 ,80 ,100,150,150,100,80 ,60 ,
	   200,250,250,300,300,250,250,200,
	   300,300,350,400,400,350,300,300,
	   400,450,500,500,500,500,450,400,
	   500,500,500,500,500,500,500,500,
	   500,500,500,500,500,500,500,500
	   
	};
	
	static int[] SafetyTableB = {				//valeur de malus pour le roi noir s'il est sur cette case
		500,500,500,500,500,500,500,500,
		500,500,500,500,500,500,500,500,
		400,450,500,500,500,500,450,400,
		300,300,350,400,400,350,300,300,
		200,250,250,300,300,250,250,200,
		60 ,80 ,100,150,150,100,80 ,60 ,
		10 ,10 ,25 ,40 ,40 ,25 ,10 ,10 ,
		0  ,0  ,10 ,20 ,20 ,10 ,0  ,0  
	};
	
	static int[] PushToEdge = //pour inciter un camp à pousser l'autre dans le coin.
		{
			60, 50, 40, 30, 30, 40, 50, 60,
			50, 40, 30, 20, 20, 30, 40, 50,
			40, 30, 20, 10, 10, 20, 30, 40,
			30, 20, 10, 0 , 0 , 10, 20, 30,
			30, 20, 10, 0 , 0 , 10, 20, 30,
			40, 30, 20, 10, 10, 20, 30, 40,
			50, 40, 30, 20, 20, 30, 40, 30,
			60, 50, 40, 30, 30, 40, 50, 60
		};
	
	public static int Evaluation(Position p)  //va retourner soit un int, soit un long
	{
		
		Eval.i++;
		
		
		
		
		float materiel_moyen = 0;     //////déclaration des variables
		int material = 0;
		int activity = 0;
		int pawnStr  = 0;
		int KingPos  = 0; int SafetyKingW = 0; int SafetyKingB = 0;
		int PosKingW = 0; int ColKingW = 0;
		int PosKingB = 0; int ColKingB = 0;
		int PosQueenW = 0; int PosQueenB = 0;
		int j = 0;
		ArrayList<Integer> PB = new ArrayList<Integer>();
		ArrayList<Integer> PN = new ArrayList<Integer>();
////////////////////////////////////////////1:Matériel (génère aussi la liste de pions, utile par la suite)
		for(int i:p.tab_position)
		{
			switch(i){      //test pour chaque case de notre échiquier (tab_position) de quelle pièce est sur la case et quelle valeur matérielle lui attribuer
			case 0:
				break;
			
			case 1:
				material += 100;
				PB.add(j);
				break;
			case -1:
				material -= 100;
				PN.add(j);
				break;
			case 3:
				material += 300; materiel_moyen += 1.5;
				break;
			case -3:
				material -= 300; materiel_moyen += 1.5;
				break;
			case 4:
				material += 350;  materiel_moyen += 1.75;
				break;
			case -4:
				material -= 350;  materiel_moyen += 1.75;
				break;
			case 5:
				material += 500;  materiel_moyen += 2.5;
				break;
			case -5:
				material -= 500;  materiel_moyen += 2.5;
				break;
			case 8:
				material += 950;  materiel_moyen += 4.75; PosQueenW = j;
				break;
			case -8:
				material -= 950;  materiel_moyen += 4.75; PosQueenB = j;
				break;
			case 9:
				SafetyKingW -= Eval.SafetyTableW[j];  //malus pour la case du roi B
				PosKingW = j; ColKingW = j%8;
				break;
			case -9:
				SafetyKingB += Eval.SafetyTableB[j];  //malus pour la case du roi N
				PosKingB = j; ColKingB = j%8;
			}
			j++;
		}
		
		if(materiel_moyen < 10) {return Eval.EndgameEval(p, materiel_moyen, material, PosKingW, PosKingB, PB, PN);} //pour optimiser, on passe en paramètre tout ce qu'on a déjà calculé
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////2: Structure de pion
		//Ce qu'on va prendre en compte: pions isolés, arriérés, passés, passé-protégés
	
	ArrayList<Integer> isoWeakSquareW = new ArrayList<Integer>(); //ArrayList des cases devant le pion faible
	ArrayList<Integer> ArrWeakSquareW = new ArrayList<Integer>(); //ArrayList des cases devant un pion arriéré
	ArrayList<Integer> PassWeakSquareW = new ArrayList<Integer>();//cases devant un pion passé
	ArrayList<Integer> WeaknessSquareW = new ArrayList<Integer>();//cases d'une faiblesse, isolé ou non
	
	
	
	ArrayList<Integer> isoWeakSquareB = new ArrayList<Integer>(); //ArrayList des cases devant le pion faible
	ArrayList<Integer> ArrWeakSquareB = new ArrayList<Integer>(); //ArrayList des cases devant un pion arriéré
	ArrayList<Integer> PassWeakSquareB = new ArrayList<Integer>();//cases devant un pion passé
	ArrayList<Integer> WeaknessSquareB = new ArrayList<Integer>();//cases d'une faiblesse, isolé ou non
	
	
	for(int pos: PB)  //boucle principale sur les pions blancs: on va juger s'ils montrent des caractéristiques particulières (passé, protégé, sur colonne ouverte, isolé, arriéré)
	{
		boolean protect = false;
		boolean pass = true;
		boolean iso = true;
		boolean arr = true;
		boolean on_open_line = true;
		
		if(pos%8 == 0)  //on trait les pions du bord séparément, ils ont besoins de moins de calcul (ici: pion à gauche)
		{
			
			////pour le roi noir (adverse), ajout d'un bonus si le pion est sur la même aile et qu'il est avancé
			if(ColKingB < 3) {if (pos>32){SafetyKingB += 60;} else if(pos>24){SafetyKingB += 40;} else if(pos>16){SafetyKingB += 20;}}
			///pour le roi blanc: ajout d'un bonus de défense s'il n'est pas avancé
			if(ColKingW < 3) {if (pos<16){SafetyKingW += 40;} else if(pos < 24){SafetyKingW += 25;}}
			
			for(int i: PB)  ///1: boucle sur les pions blancs
			{
				if(i%8  == 1)
				{
					iso = false;
					if(i-1 <= pos) {arr = false;} if(i+7 == pos) {protect = true;}
				}
			}
			for(int i:PN) ///2: boucle sur les pions noirs
			{
				if(i%8 == 0 && i>pos) {on_open_line = false; pass = false; continue;}
				if(i%8-1 == 0 && i-1>pos) {pass = false; continue;}
	//			if(i%8+1 == pos%8 && i>pos) {pass = false; continue;}  inutile pour un pion à gauche
			}
		}
				
		
		else if(pos%8 == 7)
		{
			
			////pour le roi noir (adverse), ajout d'un bonus si le pion est sur la même aile et qu'il est avancé
			if(ColKingB > 4) {if (pos>39){SafetyKingB += 60;} else if(pos>31){SafetyKingB += 40;} else if(pos>23){SafetyKingB += 20;}}
			///pour le roi blanc: ajout d'un bonus de défense s'il n'est pas avancé
			if(ColKingW > 4) {if(pos<23) {SafetyKingW += 40;} else if(pos<31){SafetyKingW += 25;}}
			
			for(int i:PB) //1: boucle sur les pions blancs
			{
				if(i%8 == 6)
				{
					iso = false;
					if(i<pos) {arr = false;} if(i+9 == pos) {protect = true;}				}
			}
			for(int i:PN) ///2: boucle sur les pions noirs
			{
				if(i%8 == 7 && i>pos) {on_open_line = false; pass = false; continue;}
			//	if(i%8-1 == pos%8 && i-1>pos) {pass = false; continue;} inutile pour un pion à droite
				if(i%8+1 == 7 && i>pos) {pass = false; continue;}
			}
		
		
		}
		else
		{
			
		////pour le roi noir (adverse), ajout d'un bonus si le pion est sur la même aile et qu'il est avancé
			if(pos%8 < 3 && ColKingB < 3) 
			{
				if(pos>39){SafetyKingB += 60;} 
				else if(pos>31){SafetyKingB += 40;} 
				else if(pos>23){SafetyKingB += 20;}
			}
			else if(pos%8>4 && ColKingB > 4)
			{
				if(pos>39){SafetyKingB += 60;} 
				else if(pos>31){SafetyKingB += 40;} 
				else if(pos>23){SafetyKingB += 20;}
			}
			///pour le roi blanc: ajout d'un bonus de défense s'il n'est pas avancé
			if(pos%8 < 3 && ColKingW < 3)
			{
				if(pos<16){SafetyKingW += 40;} 
				else if(pos<24){SafetyKingW += 25;} 
			}
			else if(pos%8>4 && ColKingW > 4)
			{
				if(pos<16){SafetyKingW += 40;} 
				else if(pos<24){SafetyKingW += 25;}
			}
			
			
			
			
			for(int i:PB) //1: boucle sur les pions blancs
			{
				if(i%8-1 == pos%8) 
				{
					iso = false; 
					if(i-1<= pos) {arr = false;}
					if(i+7 == pos){protect = true;}
				}
				if(i%8+1 == pos%8)
				{
					iso = false;
					if(i<pos) {arr = false;}
					if(i+9 == pos){protect = true;}
				}
			}
			for(int i:PN) ///2: boucle sur les pions noirs
			{
				if(i%8 == pos%8 && i>pos) {on_open_line = false; pass = false; continue;}
				if(i%8-1 == pos%8 && i-1>pos) {pass = false; continue;}
				if(i%8+1 == pos%8 && i>pos) {pass = false; continue;}
			}
		}
		
		/////Les caractéristiques du pion sont données en booléen: maintenant, on va stocker les cases importantes en fonction de la structure
		if(iso) {arr = false; isoWeakSquareW.add(pos+8); WeaknessSquareW.add(pos); pawnStr -= 30; if(on_open_line){pawnStr -= 20;}} //à voir: mais un pion isolé ne devrait pas être considéré comme arriéré
		if(arr) {ArrWeakSquareW.add(pos+8); WeaknessSquareW.add(pos); pawnStr -= 20; if(on_open_line){pawnStr -= 20;}}
		if(pass){PassWeakSquareW.add(pos+8); pawnStr += 20*((int)pos/8);} //// vraiment à voir, pas très précis
		if(protect){pawnStr += 10; if(pass){pawnStr += 30;}}
				
		
	} //fail d'indentation: mais flemme de tout réindenter...
		//MAINTENANT, structure noire
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	for(int pos: PN)  //même fonctionnement que la boucle sur les pions blancs
	{
		boolean protect = false;
		boolean pass = true;
		boolean iso = true;
		boolean arr = true;
		boolean on_open_line = true;
		
		if(pos%8 == 0)  //on trait les pions du bord séparément, ils ont besoins de moins de calcul (ici: pion à gauche)
		{
			////pour le roi blanc (adverse), ajout d'un bonus si le pion est sur la même aile et qu'il est avancé
			if(ColKingW < 3) {if(pos<24) {SafetyKingW -= 60;} else if(pos<32){SafetyKingW -= 40;} else if(pos<40){SafetyKingW -= 20;}}
			///pour le roi allié, ajout d'un bonus si les pions sont peu avancés
			if(ColKingB < 3) {if(pos>40) {SafetyKingB -= 40;} else if(pos>32){SafetyKingB -= 25;}}
			
			for(int i: PN)  ///1: boucle sur les pions noirs
			{
				if(i%8  == 1)
				{
					iso = false;
					if(i > pos) {arr = false;} if(i-9 == pos) {protect = true;}
				}
			}
			for(int i:PB) ///2: boucle sur les pions blancs
			{
				if(i%8 == 0 && i < pos) {on_open_line = false; pass = false; continue;}
				if(i%8-1 == 0 && i-1<pos) {pass = false; continue;}
	//			if(i%8+1 == pos%8 && i>pos) {pass = false; continue;}  inutile pour un pion à gauche
			}
		}
		
		
		
		
		
		else if(pos%8 == 7)
		{
			
			
			////pour le roi blanc (adverse), ajout d'un bonus si le pion est sur la même aile et qu'il est avancé
			if(ColKingW > 4) {if (pos<31){SafetyKingW -= 60;} else if(pos<39){SafetyKingW -= 40;} else if(pos<47){SafetyKingW -= 20;}}
			///pour le roi allié, ajout d'un bonus si les pions sont peu avancés
			if(ColKingB > 4) {if(pos>47){SafetyKingB -= 40;} if(pos>39) {SafetyKingB -= 25;}}
			
			for(int i:PN) //1: boucle sur les pions noirs
			{
				if(i%8 == 6)
				{
					iso = false;
					if(i+1>=pos) {arr = false;} if(i-7 == pos) {protect = true;}
				}
			}
			for(int i:PB) ///2: boucle sur les pions blancs
			{
				if(i%8 == pos%8 && i<pos) {on_open_line = false; pass = false; continue;}
			//	if(i%8-1 == pos%8 && i-1>pos) {pass = false; continue;} inutile pour un pion à droite
				if(i%8+1 == pos%8 && i<pos) {pass = false; continue;}
			}
		
		
		}
		else
		{
			
			
			////pour le roi blanc (adverse), ajout d'un bonus si le pion est sur la même aile et qu'il est avancé
			if(pos%8 < 3 && ColKingW < 3) 
			{
				if(pos<24){SafetyKingW -= 60;} 
				else if(pos<32){SafetyKingW -= 40;} 
				else if(pos<40){SafetyKingW -= 20;}
			}
			else if(pos%8>4 && ColKingB > 4)
			{
				if(pos<24){SafetyKingW -= 60;} 
				else if(pos>32){SafetyKingW -= 40;} 
				else if(pos>40){SafetyKingW -= 20;}
			}
			///pour le roi allié, ajout d'un bonus si les pions sont peu avancés
			if(pos%8 < 3 && ColKingW < 3)
			{
				if(pos>47){SafetyKingB -= 40;} 
				else if(pos>39){SafetyKingB -= 25;} 
			}
			else if(pos%8>4 && ColKingW > 4)
			{
				if(pos>47){SafetyKingB -= 40;} 
				else if(pos>39){SafetyKingB -= 25;}
			}
			
			
			
			for(int i:PN) ///1: boucle sur les pions noirs
			{
				if(i%8-1 == pos%8) 
				{
					iso = false; 
					if(i > pos) {arr = false;}
					if(i+7 == pos){protect = true;}
				}
				if(i%8+1 == pos%8)
				{
					iso = false;
					if(i+1>=pos) {arr = false;}
					if(i-7 == pos){protect = true;}
				}
			}
			for(int i:PB) ///2: boucle sur les pions blancs
			{
				if(i%8 == pos%8 && i<pos) {on_open_line = false; pass = false; continue;}
				if(i%8-1 == pos%8 && i<pos) {pass = false; continue;}
				if(i%8+1 == pos%8 && i+1<pos) {pass = false; continue;}
			}
		}
		
		/////Les caractéristiques du pion sont données en booléen: maintenant, on va stocker les cases importantes en fonction de la structure
		if(iso) {arr = false; isoWeakSquareB.add(pos-8); WeaknessSquareB.add(pos); pawnStr += 30; if(on_open_line){pawnStr += 20;}} //à voir: mais un pion isolé ne devrait pas être considéré comme arriéré
		if(arr) {ArrWeakSquareW.add(pos-8); WeaknessSquareW.add(pos); pawnStr += 20; if(on_open_line){pawnStr += 20;}}
		if(pass){PassWeakSquareW.add(pos-8); pawnStr -= 20*(8-(int)(pos/8));}//// vraiment à voir, pas très précis
		if(protect){pawnStr -= 10; if(pass){pawnStr -= 30;}}
		
				
		
		}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
/////////////////////////////////////////////3:Activité
	
	
	//attribution d'un malus, assez conséquent, si il y a beaucoup de matériel et que les pièces sont sur leur case de départ. Pareil pour les pions e et d, qui doivent bouger vite!
	//cette section est faite pour optimiser son niveau de jeu dans l'ouverture
	if(materiel_moyen > 27)
	{
		if(PosKingW == 4) {activity -= 70;}
		if((int)PosQueenW/8 > 3){activity -= 100;} //faut pas sortir sa dame trop tôt!
		if((int)PosQueenW/8 > 2){activity -= 50;}
		if((int) PosQueenW/8 > 1){activity -= 25;}
	
		if(p.tab_position[11] == 1){activity -= 100;}
		if(p.tab_position[12] == 1){activity -= 100;}
		if(p.tab_position[1] == 3) {activity -= 80;}
		if(p.tab_position[6] == 3) {activity -= 80;}
		if(p.tab_position[2] == 4) {activity -= 70;}
		if(p.tab_position[5] == 4) {activity -= 70;}
		if(p.tab_position[7] == 5) {activity -= 60;}
		if(p.tab_position[0] == 5) {activity -= 60;}
		
		if(PosKingB == 60) {activity += 70;}
		if((int)PosQueenB/8 < 4){activity += 100;}
		if((int)PosQueenB/8 < 5){activity += 50;}
		if((int) PosQueenB/8 < 6){activity += 25;}
		
		if(p.tab_position[51] == -1) {activity += 100;}
		if(p.tab_position[52] == -1) {activity += 100;}
		if(p.tab_position[57] == -3) {activity += 80;}
		if(p.tab_position[62] == -3) {activity += 80;}
		if(p.tab_position[58] == -4) {activity += 70;}
		if(p.tab_position[61] == -4) {activity += 70;}
		if(p.tab_position[56] == -5) {activity += 60;}
		if(p.tab_position[63] == -5) {activity += 60;}
	}
	
	
	
	boolean b = !p.trait; //on génère les coups légaux de l'adversaire
	double coef_controle_case;
	Position p2 = new Position(p.tab_position, p.droits_roque, b);
	p2.coups_legaux();
	
	if(p.trait) //moins de calculs à faire si on détermine d'abord à qui est le trait avant de se lancer
	{
//		activity += 10*p.liste_coups_legaux.size();
//		activity -= 10*p2.liste_coups_legaux.size();   ///retrait de ces lignes, l'activité a déjà assez d'impact comme ça
		
		
		for(int coup: p.liste_coups_legaux)
		{
			if(p.tab_position[(int)coup/100] == 8){coef_controle_case = 0.5;} else{coef_controle_case = 1;}
			activity += (int)(coef_controle_case*Eval.valeur_casesBlanc[coup%100]);
		}
		for(int c: p.cases_controllees)
		{			//cas 1: aux blancs, activité des leur pièces
//			if(p.tab_position[c] <= 0){activity += Eval.valeur_casesBlanc[c];}  //pas de bonus d'activité si une pièce contrôle une case d'une pièce de sa couleur
			for(int i: isoWeakSquareW) {if(c == i){activity += 20;break;}} /////////faut il mettre des breaks? à voir ///finalement, je crois bien que c'est mieux
			for(int i: isoWeakSquareB) {if(c == i){activity += 20;break;}}
			for(int i: ArrWeakSquareW) {if(c == i){activity += 20;break;}}
			for(int i: ArrWeakSquareB) {if(c == i){activity += 20;break;}}///dans ces lignes, valeurs attribuées aux contrôle des cases sensibles au niveau de la structure pour les blancs
			for(int i: WeaknessSquareW){if(c == i){activity += 30;break;}}
			for(int i: WeaknessSquareB){if(c == i){activity += 30;break;}}
			for(int i: PassWeakSquareW){if(c == i){activity += 20;break;}}
			for(int i: PassWeakSquareB){if(c == i){activity += 20;break;}}
			
			if(c == PosKingB+8 || c == PosKingB-8 || c == PosKingB+1 || c == PosKingB-1) {SafetyKingB += 30;} //bonus si les pièces blanches contrôlent des cases adjacentes au roi N
			if(c == PosKingW+8 || c == PosKingW-8 || c == PosKingW+1 || c == PosKingW-1) {SafetyKingW += 20;} //bonus si les pièces blanches contrôlent des cases adjacentes au roi B
			
			if(c == PosKingB+16 || c == PosKingB-16 || c == PosKingB+2 || c == PosKingB-2 || c == PosKingB +9 || c == PosKingB-9 || c == PosKingB+7 || c == PosKingB-7) {SafetyKingB += 15;} //bonus si les pièces blanches contrôlent des cases proches du roi N
			if(c == PosKingW+16 || c == PosKingW-16 || c == PosKingW+2 || c == PosKingW-2 || c == PosKingW +9 || c == PosKingW-9 || c == PosKingW+7 || c == PosKingW-7) {SafetyKingW += 10;} //bonus si les pièces blanches contrôlent des cases proches du roi B
			
			if(c == PosKingB+17 || c == PosKingB-17 || c == PosKingB+15 || c == PosKingB-15 || c == PosKingB +10 || c == PosKingB-10 || c == PosKingB+6 || c == PosKingB-6) {SafetyKingB += 6;} //bonus si les pièces blanches contrôlent des cases assez proches du roi N
			if(c == PosKingW+17 || c == PosKingW-17 || c == PosKingW+15 || c == PosKingW-15 || c == PosKingW +10 || c == PosKingW-10 || c == PosKingW+6 || c == PosKingW-6) {SafetyKingW += 4;} //bonus si les pièces blanches contrôlent des cases assez proches du roi B
		}
		for(int coup: p2.liste_coups_legaux)
		{
			if(p.tab_position[(int)coup/100] == -8){coef_controle_case = 0.5;} else{coef_controle_case = 1;}
			activity -= (int)(coef_controle_case*Eval.valeur_casesNoir[coup%100]);
		}
		for(int c: p2.cases_controllees)
		{     //aux blancs, activité pièces noires
//			if(p.tab_position[c] >= 0){activity -= Eval.valeur_casesNoir[c];} //pas de bonus d'activité si une pièce contrôle une case d'une pièce de sa couleur
			for(int i: isoWeakSquareW) {if(c == i){activity -= 20; break;}}
			for(int i: isoWeakSquareB) {if(c == i){activity -= 20; break;}}
			for(int i: ArrWeakSquareW) {if(c == i){activity -= 20; break;}}
			for(int i: ArrWeakSquareB) {if(c == i){activity -= 20; break;}}///dans ces lignes, valeurs attribuées aux contrôle des cases sensibles au niveau de la structure pour les noirs
			for(int i: WeaknessSquareW){if(c == i){activity -= 30; break;}}
			for(int i: WeaknessSquareB){if(c == i){activity -= 30; break;}}
			for(int i: PassWeakSquareW){if(c == i){activity -= 20; break;}}
			for(int i: PassWeakSquareB){if(c == i){activity -= 20; break;}}
			
			if(c == PosKingW) {SafetyKingW -= 50;} //bonus si roi blanc en échec
			
			if(c == PosKingB+8 || c == PosKingB-8 || c == PosKingB+1 || c == PosKingB-1) {SafetyKingB -= 20;} //bonus si les pièces noires contrôlent des cases adjacentes au roi N
			if(c == PosKingW+8 || c == PosKingW-8 || c == PosKingW+1 || c == PosKingW-1) {SafetyKingW -= 30;} //bonus si les pièces noires contrôlent des cases adjacentes au roi B
			
			if(c == PosKingB+16 || c == PosKingB-16 || c == PosKingB+2 || c == PosKingB-2 || c == PosKingB +9 || c == PosKingB-9 || c == PosKingB+7 || c == PosKingB-7) {SafetyKingB -= 10;} //bonus si les pièces noires contrôlent des cases proches du roi N
			if(c == PosKingW+16 || c == PosKingW-16 || c == PosKingW+2 || c == PosKingW-2 || c == PosKingW +9 || c == PosKingW-9 || c == PosKingW+7 || c == PosKingW-7) {SafetyKingW -= 15;} //bonus si les pièces noires contrôlent des cases proches du roi B
			
			if(c == PosKingB+17 || c == PosKingB-17 || c == PosKingB+15 || c == PosKingB-15 || c == PosKingB +10 || c == PosKingB-10 || c == PosKingB+6 || c == PosKingB-6) {SafetyKingB -= 4;} //bonus si les pièces noires contrôlent des cases assez proches du roi N
			if(c == PosKingW+17 || c == PosKingW-17 || c == PosKingW+15 || c == PosKingW-15 || c == PosKingW +10 || c == PosKingW-10 || c == PosKingW+6 || c == PosKingW-6) {SafetyKingW -= 6;} //bonus si les pièces noires contrôlent des cases assez proches du roi B
			
		}
	}
	else
	{
//		activity -= 10*p.liste_coups_legaux.size();           ///retrait de ces lignes, l'activité a déjà assez d'impact comme ça
//		activity += 10*p2.liste_coups_legaux.size();
		for(int coup: p2.liste_coups_legaux)
		{
			if(p.tab_position[(int)coup/100] == 8){coef_controle_case = 0.5;} else{coef_controle_case = 1;}
			activity += (int)(coef_controle_case*Eval.valeur_casesBlanc[coup%100]);
		}
		for(int c: p2.cases_controllees)
		{   //aux noirs, activité pièces blanches
//			if(p.tab_position[c] <= 0){activity += Eval.valeur_casesBlanc[c];} //pas de bonus d'activité si une pièce contrôle une case d'une pièce de sa couleur
			for(int i: isoWeakSquareW) {if(c == i){activity += 20;break;}}
			for(int i: isoWeakSquareB) {if(c == i){activity += 20;break;}}
			for(int i: ArrWeakSquareW) {if(c == i){activity += 20;break;}}
			for(int i: ArrWeakSquareB) {if(c == i){activity += 20;break;}}///dans ces lignes, valeurs attribuées aux contrôle des cases sensibles au niveau de la structure pour les noirs
			for(int i: WeaknessSquareW){if(c == i){activity += 30;break;}}
			for(int i: WeaknessSquareB){if(c == i){activity += 30;break;}}
			for(int i: PassWeakSquareW){if(c == i){activity += 20;break;}}
			for(int i: PassWeakSquareB){if(c == i){activity += 20;break;}}
			
			if(c == PosKingB) {SafetyKingB += 50;} //bonus si roi noir en échec
			
			if(c == PosKingB+8 || c == PosKingB-8 || c == PosKingB+1 || c == PosKingB-1) {SafetyKingB += 30;} //bonus si les pièces blanches contrôlent des cases adjacentes au roi N
			if(c == PosKingW+8 || c == PosKingW-8 || c == PosKingW+1 || c == PosKingW-1) {SafetyKingW += 20;} //bonus si les pièces blanches contrôlent des cases adjacentes au roi B
			
			if(c == PosKingB+16 || c == PosKingB-16 || c == PosKingB+2 || c == PosKingB-2 || c == PosKingB +9 || c == PosKingB-9 || c == PosKingB+7 || c == PosKingB-7) {SafetyKingB += 15;} //bonus si les pièces blanches contrôlent des cases proches du roi N
			if(c == PosKingW+16 || c == PosKingW-16 || c == PosKingW+2 || c == PosKingW-2 || c == PosKingW +9 || c == PosKingW-9 || c == PosKingW+7 || c == PosKingW-7) {SafetyKingW += 10;} //bonus si les pièces blanches contrôlent des cases proches du roi B
			
			if(c == PosKingB+17 || c == PosKingB-17 || c == PosKingB+15 || c == PosKingB-15 || c == PosKingB +10 || c == PosKingB-10 || c == PosKingB+6 || c == PosKingB-6) {SafetyKingB += 6;} //bonus si les pièces blanches contrôlent des cases assez proches du roi N
			if(c == PosKingW+17 || c == PosKingW-17 || c == PosKingW+15 || c == PosKingW-15 || c == PosKingW +10 || c == PosKingW-10 || c == PosKingW+6 || c == PosKingW-6) {SafetyKingW += 4;} //bonus si les pièces blanches contrôlent des cases assez proches du roi B
		}
		for(int coup: p.liste_coups_legaux)
		{
			if(p.tab_position[(int)coup/100] == -8){coef_controle_case = 0.5;} else{coef_controle_case = 1;}
			activity -= (int)(coef_controle_case*Eval.valeur_casesNoir[coup%100]);
		}
		for(int c: p.cases_controllees)
		{   ///aux noirs, activité des pièces noires
//			if(p.tab_position[c] >= 0){activity -= Eval.valeur_casesNoir[c];} //pas de bonus d'activité si une pièce contrôle une case d'une pièce de sa couleur
			
			for(int i: isoWeakSquareW) {if(c == i){activity -= 20;break;}}
			for(int i: isoWeakSquareB) {if(c == i){activity -= 20;break;}}
			for(int i: ArrWeakSquareW) {if(c == i){activity -= 20;break;}}
			for(int i: ArrWeakSquareB) {if(c == i){activity -= 20;break;}}///dans ces lignes, valeurs attribuées au contrôle des cases sensibles au niveau de la structure pour les noirs
			for(int i: WeaknessSquareW){if(c == i){activity -= 30;break;}}
			for(int i: WeaknessSquareB){if(c == i){activity -= 30;break;}}
			for(int i: PassWeakSquareW){if(c == i){activity -= 20;break;}}
			for(int i: PassWeakSquareB){if(c == i){activity -= 20;break;}}
			
			if(c == PosKingB+8 || c == PosKingB-8 || c == PosKingB+1 || c == PosKingB-1) {SafetyKingB -= 20;} //bonus si les pièces noires contrôlent des cases adjacentes au roi N
			if(c == PosKingW+8 || c == PosKingW-8 || c == PosKingW+1 || c == PosKingW-1) {SafetyKingW -= 30;} //bonus si les pièces noires contrôlent des cases adjacentes au roi B
			
			if(c == PosKingB+16 || c == PosKingB-16 || c == PosKingB+2 || c == PosKingB-2 || c == PosKingB +9 || c == PosKingB-9 || c == PosKingB+7 || c == PosKingB-7) {SafetyKingB -= 10;} //bonus si les pièces blanches contrôlent des cases proches du roi N
			if(c == PosKingW+16 || c == PosKingW-16 || c == PosKingW+2 || c == PosKingW-2 || c == PosKingW +9 || c == PosKingW-9 || c == PosKingW+7 || c == PosKingW-7) {SafetyKingW -= 15;} //bonus si les pièces blanches contrôlent des cases proches du roi B
			
			if(c == PosKingB+17 || c == PosKingB-17 || c == PosKingB+15 || c == PosKingB-15 || c == PosKingB +10 || c == PosKingB-10 || c == PosKingB+6 || c == PosKingB-6) {SafetyKingB -= 4;} //bonus si les pièces blanches contrôlent des cases assez proches du roi N
			if(c == PosKingW+17 || c == PosKingW-17 || c == PosKingW+15 || c == PosKingW-15 || c == PosKingW +10 || c == PosKingW-10 || c == PosKingW+6 || c == PosKingW-6) {SafetyKingW -= 6;} //bonus si les pièces blanches contrôlent des cases assez proches du roi B
			
		}
	}
	activity = (int)activity/2; //elle était surévaluée, la diviser par 2 semble être une solution
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
//////////////////////////////////////////////GESTION DE LA POSITION DU ROI
	

	
	//le coefficient d'attaque au roi par rapport au matériel moyen (Dans le cas où il est inférieur à 10, la fonction n'est pas appelée du tout, donc à considérer dans l'intervalle 10-30)	
	double coeff  = (0.003*materiel_moyen*materiel_moyen+0.001*materiel_moyen+0.335)/3;
	if(SafetyKingW > 0){SafetyKingW = 0;} ///pas de bonus si on a le roi trop protégé, sauf pour le fait d'être déroqué!
	if(SafetyKingB < 0){SafetyKingB = 0;}
	
	if(ColKingW < 5 && ColKingW > 2)
	{
		SafetyKingW -= 25;
		if(!p.droits_roque[0] && !p.droits_roque[1]){SafetyKingW -= 50;}
	}
	if(ColKingB < 5 && ColKingB > 2)
	{
		SafetyKingB += 25;
		if(!p.droits_roque[2] && !p.droits_roque[3]){SafetyKingB += 50;}
	}
	

	KingPos = (int) ((SafetyKingW+SafetyKingB)*coeff);




	return pawnStr+material+activity+KingPos;
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////FINALES ////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int EndgameEval(Position p, float materiel_moyen, int materiel, int PosKingW, int PosKingB, ArrayList<Integer> PB, ArrayList<Integer> PN)
	
	{
		int pawnStr = 0;
		int activity = 0;

		
		if(PB.isEmpty() && PN.isEmpty() || (materiel > 200 && PB.isEmpty() || materiel < -200 && PN.isEmpty())) //s'il n'y a pas de pion, ou du moins pas de pion pour celui qui a l'avantage matériel, les choses se passent differemment
		{
			if(materiel<0){activity -= Eval.PushToEdge[PosKingW]; activity += Eval.PushToEdge[PosKingB];}
			if(materiel>0){activity += Eval.PushToEdge[PosKingB]; activity -= Eval.PushToEdge[PosKingW];}
			
			if(materiel < 300 && materiel > -300)
			{
				return (int)((materiel+activity)/15);
			}
			
			else if(materiel < 400 && materiel > -400)
			{
				if(materiel_moyen >= 6.75) //pour prendre en compte TF contre T
				{
					return (int)((materiel+activity)/2);
				}
				else
				{
					return (int)((materiel+activity)/6);
				}
			}
			else 
			{
				if((materiel == 600 || materiel == -600) && materiel_moyen == 3){return 0;} 
				return materiel+activity;
			}
		}
		
		else
		{
			
	//ici: code principal d'EndgameEval		
	
			//étape 1: structure de pions. On peut reprendre en très grande partie la fonction de l'éval normale		
			//ArrayList<Integer> WeakSquareW = new ArrayList<Integer>(); //ArrayList des cases devant un pion faible
			ArrayList<Integer> PassWeakSquareW = new ArrayList<Integer>();//cases devant un pion passé, on retrouve le pion passé grâce à elle
			ArrayList<Integer> WeaknessSquareW = new ArrayList<Integer>();//cases d'une faiblesse, isolé ou non
			
			

			//ArrayList<Integer> WeakSquareB = new ArrayList<Integer>(); //ArrayList des cases devant un pion faible
			ArrayList<Integer> PassWeakSquareB = new ArrayList<Integer>();//cases devant un pion passé
			ArrayList<Integer> WeaknessSquareB = new ArrayList<Integer>();//cases d'une faiblesse, isolé ou non
			
			
			for(int pos: PB)  //boucle principale sur les pions blancs: on va juger s'ils montrent des caractéristiques particulières (passé, protégé, sur colonne ouverte, isolé, arriéré)
			{
				boolean protect = false;
				boolean pass = true;
				boolean weak = true;
				
				if(pos%8 == 0)  //on trait les pions du bord séparément, ils ont besoins de moins de calcul (ici: pion à gauche)
				{
					for(int i: PB)  ///1: boucle sur les pions blancs
					{
						if(i%8  == 1)
						{
							if(i-1 <= pos) {weak = false;} if(i+7 == pos) {protect = true;}
						}
					}
					for(int i:PN) ///2: boucle sur les pions noirs
					{
						if(i%8 == 0 && i>pos) {pass = false; continue;}
						if(i%8-1 == 0 && i-1>pos) {pass = false; continue;}
			//			if(i%8+1 == pos%8 && i>pos) {pass = false; continue;}  inutile pour un pion à gauche
					}
				}
						
				
				else if(pos%8 == 7)
				{	
					for(int i:PB) //1: boucle sur les pions blancs
					{
						if(i%8 == 6)
						{
							if(i<pos) {weak = false;} if(i+9 == pos) {protect = true;}
						}
					}
					for(int i:PN) ///2: boucle sur les pions noirs
					{
						if(i%8 == 7 && i>pos) {pass = false; continue;}
					//	if(i%8-1 == pos%8 && i-1>pos) {pass = false; continue;} inutile pour un pion à droite
						if(i%8+1 == 7 && i>pos) {pass = false; continue;}
					}
				
				
				}
				else
				{					
					for(int i:PB) //1: boucle sur les pions blancs
					{
						if(i%8-1 == pos%8) 
						{
							if(i-1<= pos) {weak = false;}
							if(i+7 == pos){protect = true;}
						}
						if(i%8+1 == pos%8)
						{
							if(i<pos) {weak = false;}
							if(i+9 == pos){protect = true;}
						}
					}
					for(int i:PN) ///2: boucle sur les pions noirs
					{
						if(i%8 == pos%8 && i>pos) {pass = false; continue;}
						if(i%8-1 == pos%8 && i-1>pos) {pass = false; continue;}
						if(i%8+1 == pos%8 && i>pos) {pass = false; continue;}
					}
				}			
				/////Les caractéristiques du pion sont données en booléen: maintenant, on va stocker les cases importantes en fonction de la structure
				if(weak) {WeaknessSquareW.add(pos); pawnStr -= 30;} //à voir: mais un pion isolé ne devrait pas être considéré comme arriéré
				if(pass){PassWeakSquareW.add(pos+8); pawnStr += 25*(pos-pos%8)/8;} //// vraiment à voir, pas très précis
				if(protect){pawnStr += 20; if(pass){pawnStr += 50;}}
						
				
			} 
				//MAINTENANT, structure noire
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			for(int pos: PN)  //même fonctionnement que la boucle sur les pions blancs
			{
				boolean protect = false;
				boolean pass = true;
				boolean weak = true;
				
				if(pos%8 == 0)  //on trait les pions du bord séparément, ils ont besoins de moins de calcul (ici: pion à gauche)
				{
					for(int i: PN)  ///1: boucle sur les pions noirs
					{
						if(i%8  == 1)
						{
							if(i > pos) {weak = false;} if(i-9 == pos) {protect = true;}
						}
					}
					for(int i:PB) ///2: boucle sur les pions blancs
					{
						if(i%8 == 0 && i < pos) {pass = false; continue;}
						if(i%8-1 == 0 && i-1<pos) {pass = false; continue;}
			//			if(i%8+1 == pos%8 && i>pos) {pass = false; continue;}  inutile pour un pion à gauche
					}
				}
				
				else if(pos%8 == 7)
				{
					for(int i:PN) //1: boucle sur les pions noirs
					{
						if(i%8 == 6)
						{
							if(i+1>=pos) {weak = false;} if(i-7 == pos) {protect = true;}
						}
					}
					for(int i:PB) ///2: boucle sur les pions blancs
					{
						if(i%8 == pos%8 && i<pos) {pass = false; continue;}
					//	if(i%8-1 == pos%8 && i-1>pos) {pass = false; continue;} inutile pour un pion à droite
						if(i%8+1 == pos%8 && i<pos) {pass = false; continue;}
					}
				
				
				}
				else
				{
					for(int i:PN) ///1: boucle sur les pions noirs
					{
						if(i%8-1 == pos%8) 
						{ 
							if(i > pos) {weak = false;}
							if(i+7 == pos){protect = true;}
						}
						if(i%8+1 == pos%8)
						{
							if(i+1>=pos) {weak = false;}
							if(i-7 == pos){protect = true;}
						}
					}
					for(int i:PB) ///2: boucle sur les pions blancs
					{
						if(i%8 == pos%8 && i<pos) {pass = false; continue;}
						if(i%8-1 == pos%8 && i<pos) {pass = false; continue;}
						if(i%8+1 == pos%8 && i+1<pos) {pass = false; continue;}
					}
				}
				
				/////Les caractéristiques du pion sont données en booléen: maintenant, on va stocker les cases importantes en fonction de la structure
				if(weak) {WeaknessSquareB.add(pos); pawnStr += 30;} //à voir: mais un pion isolé ne devrait pas être considéré comme arriéré
				if(pass){PassWeakSquareB.add(pos-8); pawnStr -= 25*(8-((int)pos/8));}//// vraiment à voir, pas très précis
				if(protect){pawnStr -= 20; if(pass){pawnStr -= 50;}}		
			}

			//voilà qui est fait. Maintenant, c'est l'activité qui doit être prise en compte.
			//comme avant, on génère les coups légaux de l'adversaire:
			Position p2 = new Position(p.tab_position, p.droits_roque, !p.trait);
			p2.coups_legaux();
			


			//activity += tab_PosKingW[PosKingW];   ///Ces deux tableaux sont évidemment fixes. 
			//activity -= tab_PosKingB[PosKingB];
			
			
			
			if(p.trait)
			{
				activity += 10*p.liste_coups_legaux.size();
				activity -= 10*p2.liste_coups_legaux.size();
								
				for(int c: p.cases_controllees)
				{
					if(p.tab_position[c] < -2) {activity += 20;}  //la probabilité que ce soit juste de rajouter 20 est grande
					for(int i: WeaknessSquareW) {if(c==i) {activity += 20;} else if(c==i+8){activity += 30;}}  //chiffres à déterminer
					for(int i: WeaknessSquareB) {if(c==i) {activity += 40;} else if(c==i-8){activity += 30;}}
					//for(int i: PassWeakSquareW) {if(c==i) activity += 40; else if(c-8 == i) activity += 25;} déjà compté plus bas
					//for(int i: PassWeakSquareB) {if(c==i) activity += 30; else if(c+8 == i) activity += 20;} déjà compté plus bas
					
					if(c == PosKingB){activity += 40;}
				}
				for(int c: p2.cases_controllees)
				{
					if(p.tab_position[c] > 2) {activity -= 20;}
					for(int i: WeaknessSquareW) {if(c==i) {activity -= 40;} else if(c==i+8){activity -= 30;}}  //chiffres à déterminer
					for(int i: WeaknessSquareB) {if(c==i) {activity -= 20;} else if(c==i-8){activity -= 30;}}
					//for(int i: PassWeakSquareW) {if(c==i) activity -= 30; else if(c-8 == i) activity -= 20;} déjà compté plus bas
					//for(int i: PassWeakSquareB) {if(c==i) activity -= 40; else if(c+8 == i) activity -= 25;}
				}				
				
			}
			else
			{
				activity += 10*p2.liste_coups_legaux.size();
				activity -= 10*p.liste_coups_legaux.size();
				
				for(int c: p.cases_controllees)
				{
					if(p.tab_position[c] > 2) {activity -= 20;}
					for(int i: WeaknessSquareW) {if(c==i) {activity -= 40;} else if(c==i+8){activity -= 30;}}  //chiffres à déterminer  
					for(int i: WeaknessSquareB) {if(c==i) {activity -= 20;} else if(c==i-8){activity -= 30;}}
					//for(int i: PassWeakSquareW) {if(c==i) activity -= 30; else if(c-8 == i) activity -= 20;}
					//for(int i: PassWeakSquareB) {if(c==i) activity -= 40; else if(c+8 == i) activity -= 25;}
					if(c == PosKingW){activity -= 40;}
				}
				for(int c: p2.cases_controllees) //cases controllees par les blancs
				{
					if(p.tab_position[c] < -2) {activity += 20;}
					for(int i: WeaknessSquareB) {if(c==i) {activity += 40;} else if(c==i+8){activity += 30;}}  //chiffres à déterminer
					for(int i: WeaknessSquareW) {if(c==i) {activity += 20;} else if(c==i-8){activity += 30;}}
					

				}
			}

			//Il faut maintenant tester le danger des pions passés. Une étape cruciale
			float PPcontrol = 0;
			for(int i: PassWeakSquareW)
			{
				PPcontrol = 1;
				//1:ROI  voir si le roi est dans le carré ou pas:
				if(Eval.carre(PosKingB, i-8, true))
				{
					PPcontrol -= 2;
				}
				if(Eval.carre(PosKingW, i-8, true))
				{
					PPcontrol += 2;
				}
				if(p.trait)
				{	
					for(int c: p.cases_controllees)
					{
						if((c-i)%8 == 0 && c >= i){PPcontrol += 16/(c-i+8);}	
					}
					for(int c: p2.cases_controllees)
					{
						if((c-i)%8 == 0 && c >= i){PPcontrol -= 16/(c-i+8);}
					}
				}
				else
				{
					for(int c: p.cases_controllees)
					{
						if((c-i)%8 == 0 && c > i){PPcontrol -= 16/(c-i+8);}	
					}
					for(int c: p2.cases_controllees)
					{
						if((c-i)%8 == 0 && c > i){PPcontrol += 16/(c-i+8);}
					}
				}
				
				/////ICI: fonction qui détermine le danger en fonction de PPcontrol et de la case du pion
				activity += (int)(PPcontrol*50/(8-(int)i/8)); //bof, pas terrible du tout, à revoir
			}
			
			
				
			for(int i: PassWeakSquareB)
			{
				PPcontrol = -1;
				//1:ROI  voir si le roi est dans le carré ou pas:
				if(Eval.carre(PosKingB, i+8, false))
				{
					PPcontrol -= 2;
				}
				if(Eval.carre(PosKingW, i+8, false))
				{
					PPcontrol += 2;
				}
				if(p.trait)
				{	
					for(int c: p.cases_controllees)
					{
						if((c-i)%8 == 0 && c < i){PPcontrol += 16/(c-i-8);}	
					}
					for(int c: p2.cases_controllees)
					{
						if((c-i)%8 == 0 && c < i){PPcontrol -= 16/(c-i-8);}
					}
				}
				else
				{
					for(int c: p.cases_controllees)
					{
						if((c-i)%8 == 0 && c < i){PPcontrol -= 16/(c-i-8);}	
					}
					for(int c: p2.cases_controllees)
					{
						if((c-i)%8 == 0 && c < i){PPcontrol += 16/(c-i-8);}
					}
				}
				
				/////ICI: fonction qui détermine le danger en fonction de PPcontrol et de la case du pion
				activity += (int)(PPcontrol*50/((int)i/8+1)); //bof, pas terrible du tout, à revoir
				
				
				
			
				
			}
	
			
			
		}

		
//etape 2: structure de pion:FAIT

//etape 3: pions passés:FAIT
		
//etape 4: placement des pièces: C'est leur cases controllées qui sont pris en compte, le placement n'a pas beaucoup de sens en finales.


		
		return activity+materiel+pawnStr;
	}
	
	
	
	
	
	
	//vérifie si un roi est dans le carré d'un pion
	public static boolean carre(int posK, int posP, boolean color) //pos du pion
	{
		if(color)
		{
			if(posK-posP < -6) {return false;} //toujours vrai
			if(posK%8 >= posP%8)
			{
				if(posK%8-posP%8 > 8-(int)(posP/8)) {return false;} else{return true;}
			} //si le roi N est à droite du pion (côté blancs) et que son écart au pion est plus grand que le nombre de cases entre la promotion et le pion
			else
			{
				if(posP%8-posK%8 > 8-(int)(posP/8)) {return false;} else{return true;}				
			} //si le roi N est à gauche du pion et que son écart au pion est plus grand que le nombre de cases entre la promotion et le pion 
		}
		else
		{
			if(posP-posK < -6) {return false;}
			if(posK%8 >= posP%8)
			{
				if(posK%8-posP%8 > (int)(posP/8)) {return false;} else{return true;}
			} //si le roi N est à droite du pion (côté blancs) et que son écart au pion est plus grand que le nombre de cases entre la promotion et le pion
			else
			{
				if(posP%8-posK%8 > (int)(posP/8)) {return false;} else{return true;}				
			} //si le roi N est à gauche du pion et que son écart au pion est plus grand que le nombre de cases entre la promotion et le pion 
		}
	}
	
	
	
	
	
	
	

}