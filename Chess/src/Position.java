import java.util.ArrayList;

/////////////BUT: ne pas créer de liste de pieces, instancier avec un tableau, pas de liste de coups pseudo légaux, en gros, toute la génération des coups se fait sans classes pièces
/////////////ATTENTION: dans ce modèle, le roi vaut 9 , la dame vaut 8
public class Position {

	static int N = 0;
	static int[] ValPiece = {-950, 0, 0, -500, -350, -300, 0, -100, 0, 100, 0, 300, 350, 500, 0, 0, 950}; //pour retrouver la valeur d'une pièce facilement
	
	boolean[] droits_roque; //1:grand roque blanc 2:pt roque blanc 3:gd roque noir 4:pt roque noir
	boolean trait;
	int[] tab_position;
	ArrayList<Integer> liste_coups_legaux = new ArrayList<Integer>();
	ArrayList<Integer> cases_controllees = new ArrayList<Integer>();
	int passant_weak_square = -1; //case permettant une prise en passant
	//ArrayList<Position> positions_filles = new ArrayList<Position>(); A ENLEVER  La référence empêche la destruction des objets!
	

	public Position(int[] pos, boolean[] bool, boolean trait)  //la clé fonctionnerait ainsi: (pour la pos de départ par exemple)
	{						 
		this.droits_roque = bool;
		this.tab_position = pos; //remplis le tableau tab_position
		this.trait = trait;
		
//		this.liste_coups_pseudo_legaux = trouver_coups_pseudo_legaux();  //un jour, peutêtre, cette liste n'existera pas, elle sera envoyée en paramètre à la fonction trouver_coups_legaux et aux autres fonctions qui la demande (elle est assez lourde)
	}																	//Utile dans n'importe quel cas ou on instancie une position
	
	public ArrayList<Integer> trouver_coups_pseudo_legaux()   /////besoin de rien pour cette fonction
	{
		Position.N++;
		
		
		ArrayList<Integer> c = new ArrayList<Integer>();
		if(this.trait){
			for(int i = 0; i<64; i++)
			{
				if(tab_position[i] == 1){c.addAll(CoupsPion(i, true));}
				if(tab_position[i] == 3){c.addAll(CoupsCava(i, true));}
				if(tab_position[i] == 4){c.addAll(CoupsFou(i, true));}
				if(tab_position[i] == 5){c.addAll(CoupsTour(i, true));}
				if(tab_position[i] == 8){c.addAll(CoupsDame(i, true));}
				if(tab_position[i] == 9){c.addAll(CoupsRoi(i, true));}
			}
		}
		else
		{
	
			for(int i = 0; i<64; i++)
			{
				if(tab_position[i] == -1){c.addAll(CoupsPion(i, true));}
				if(tab_position[i] == -3){c.addAll(CoupsCava(i, true));}
				if(tab_position[i] == -4){c.addAll(CoupsFou(i, true));}
				if(tab_position[i] == -5){c.addAll(CoupsTour(i, true));}
				if(tab_position[i] == -8){c.addAll(CoupsDame(i, true));}
				if(tab_position[i] == -9){c.addAll(CoupsRoi(i, true));}
			}
		}
		return c;
	}		

	public ArrayList<Position> coups_legaux(){   //rend exacte la liste des coups pseudo legaux, et les positions filles seront juste renvoyées, mais leurs coups legaux ne seront pas trouvés. 
								  //les coups legaux des positions filles seront gérées dans le minimax.
		
		ArrayList<Integer> CoupsPseudoLegaux = this.trouver_coups_pseudo_legaux();
		ArrayList<Integer> a = new ArrayList<Integer>(); //la liste des coups
		ArrayList<Position> b = new ArrayList<Position>(); //la liste des positions filles
		for(int coup: CoupsPseudoLegaux)
		{
			Position p = this.SimulerCoup(coup);
			if(p.Legal()){a.add(coup); b.add(p);}
		}
		this.liste_coups_legaux = a;
		return b;
	}
	
	public boolean Legal()					//////// teste si la couleur au trait menace échec
	{	
		
		int c = 0;
		for(int i:this.tab_position)
		{
			if((i == -9 && this.trait)||(i == 9 && !this.trait))
			{
				return !this.isSquareControlled(c, this.trait);
			}
			c++;
		}
		
		return false;
	}
	
	
	public Position SimulerCoup(int c)  //simule chaque type de coup séparément, on modifie la clé et on réinstancie une nouvelle position, pour ne pas copier les références
										 //ATTENTION: cette fonction va buguer si on essaie de simuler un coups noir alors que c'est aux blancs (on utilise this.trait)
	{
		int[] tab_pos = this.tab_position.clone();
		boolean tr = !this.trait; //////////on modifie le trait, ensuite, on simulera chaque coup séparément
		boolean[] roque = this.droits_roque.clone();
		if(c == 0){return new Position(tab_pos, roque, tr);} //le coup 0 est un coup nul, selon mon programme
/////////////////////////		
		if(c == 402 && this.tab_position[4] == 9) //GRAND ROQUE BLANC
		{
				tab_pos[4] = 0;	 //déplacement roi
				tab_pos[2] = 9;  //déplacement roi
				tab_pos[0] = 0;  //déplacement de la
				tab_pos[3] = 5;  //tour
		}	
		else if(c == 406 && this.tab_position[4] == 9) //petit roque blanc
		{
			tab_pos[4] = 0;
			tab_pos[6] = 9;
			tab_pos[7] = 0;
			tab_pos[5] = 5;
		}
		else if(c == 6058 && this.tab_position[60] == -9) //grand roque noir
		{
			tab_pos[60] = 0;
			tab_pos[58] = -9;
			tab_pos[56] = 0;
			tab_pos[59] = -5;
		
		}
		else if(c == 6062 && this.tab_position[60] == -9) //petit roque noir
		{
			tab_pos[60] = 0;
			tab_pos[62] = -9;
			tab_pos[63] = 0;
			tab_pos[61] = -5;
		}
		else if(c%100 == this.passant_weak_square && this.tab_position[(int)c/100] == 1) //PRISE EN PASSANT BLANCHE
		{
			tab_pos[(int)c/100] = 0;
			tab_pos[c%100] = 1;
			tab_pos[(c%100)-8] = 0;
		}
		else if(c%100 == this.passant_weak_square && this.tab_position[(int)c/100] == -1) //prise en passant noire
		{
			tab_pos[(int)c/100] = 0;
			tab_pos[c%100] = -1;
			tab_pos[(c%100)+8] = 0;
		}
		else if(c%100 > 55 && this.tab_position[(int)c/100] == 1)//promotion blanche
		{
			tab_pos[c%100] = 8;
			tab_pos[(int)c/100] = 0;
		}
		else if(c%100<8 && this.tab_position[(int)c/100] == -1)
		{
			tab_pos[c%100] = -8;
			tab_pos[(int)c/100] = 0;
		}
		
		else //le reste
		{
			int cd = (int)c/100; //la case de départ
			if(cd == 4) {roque[0] = false; roque[1] = false;}        ////gère les droits de roque
			if(cd == 0) {roque[0] = false;}
			if(cd == 7) {roque[1] = false;}
			if(cd == 60){roque[2] = false; roque[3] = false;}
			if(cd == 56){roque[2] = false;}
			if(cd == 63){roque[3] = false;}
			
			tab_pos[c%100] = this.tab_position[cd];			
			tab_pos[(int)c/100] = 0;	
			
			
		}
		
		Position p = new Position(tab_pos, roque, tr);
		if(this.tab_position[(int)c/100] == 1 && c%100-(int)c/100 == 16) {p.passant_weak_square = c%100-8;}
		if(this.tab_position[(int)c/100] == -1 && (int)c/100-c%100 == 16) {p.passant_weak_square = c%100+8;}
		
		return p;
	
	}
		
//////////////////////////////// COUPS LEGAUX DE CHAQUE PIECE
////////////////////////////////
	
	

	ArrayList<Integer> CoupsCava(int pos, boolean b) { ///B est VRAI si on la fonction n'est pas appelée par IsControlled
		ArrayList<Integer> liste = new ArrayList<Integer>();
		
		if((pos+15)%8 < 7 && pos+15 < 64) {if(b){this.cases_controllees.add(pos+15);} if(this.tab_position[pos+15]*this.tab_position[pos] <= 0) {liste.add(101*pos+15);}}
		if((pos+17)%8 > 0 && pos+17 < 64) {if(b){this.cases_controllees.add(pos+17);} if(this.tab_position[pos+17]*this.tab_position[pos] <= 0) {liste.add(101*pos+17);}}
		if((pos+10)%8 > 1 && pos+10 < 64) {if(b){this.cases_controllees.add(pos+10);} if(this.tab_position[pos+10]*this.tab_position[pos] <= 0) {liste.add(101*pos+10);}}
		if((pos- 6)%8 > 1 && pos-6  >= 0) {if(b){this.cases_controllees.add(pos-6);}  if(this.tab_position[pos- 6]*this.tab_position[pos] <= 0) {liste.add(101*pos-6);}}
		if((pos-15)%8 > 0 && pos-15 >= 0) {if(b){this.cases_controllees.add(pos-15);} if(this.tab_position[pos-15]*this.tab_position[pos] <= 0) {liste.add(101*pos-15);}}
		if((pos-17)%8 < 7 && pos-17 >= 0) {if(b){this.cases_controllees.add(pos-17);} if(this.tab_position[pos-17]*this.tab_position[pos] <= 0) {liste.add(101*pos-17);}}
		if((pos-10)%8 < 6 && pos-10 >= 0) {if(b){this.cases_controllees.add(pos-10);} if(this.tab_position[pos-10]*this.tab_position[pos] <= 0) {liste.add(101*pos-10);}}
		if((pos+ 6)%8 < 6 && pos+ 6 < 64) {if(b){this.cases_controllees.add(pos+6);}  if(this.tab_position[pos+6 ]*this.tab_position[pos] <= 0) {liste.add(101*pos+6);}}

		return liste;
	}
	
	
	
	
	
	
	ArrayList<Integer> CoupsPion(int position, boolean b) {
		ArrayList<Integer> liste_coups = new ArrayList<Integer>();
		
		//EXPLICATION: En premier on test si coup devant possible
		// on test si un coup de 2 devant est possible, ensuite on test les promotions, et si pas promotion on rajoute le coup normal
		// pour les prises, on test si prise possible, des deux côtés, ensuite on regarde si promotion, sinon on rajoute coup
		//ensuite, pour les prises, on regarde si la case est une case faible de PeP
		
		if (this.trait)
		{
			if(this.tab_position[position+8] == 0) 
			{
				
				if(position > 7 && position < 16 && this.tab_position[position+16] == 0) {liste_coups.add(position*100+position+16);}
			//	if(position > 47 && position < 56) {}  On ne va pas simuler plusieurs coups pour la promotion, ça serait possible, mais partons du principe que la promotion est toujours dame

				liste_coups.add(position*101+8);
				
			}
			
			if(position%8 != 0)
			{
				if(b){this.cases_controllees.add(position+7);}
				
				if(this.tab_position[position+7] < 0)
				{
					//if(position > 47 && position < 56){}
					liste_coups.add(position*101+7);

				}
			}
			
			if(position%8 != 7)
			{
				if(b){this.cases_controllees.add(position+9);}
				
				if(this.tab_position[position+9] < 0)
				{
					//if(position > 47 && position < 56){}					
					liste_coups.add(100*position+position+9);

				}
				if(position+7 == this.passant_weak_square) {liste_coups.add(101*position+7);} //on pourrait mettre 100*pos+(pos+7)
				if(position+9 == this.passant_weak_square) {liste_coups.add(101*position+9);}
				/////////////////
			}

	
		}
		else
		{
			if(this.tab_position[position-8] == 0) {
				
				if(position > 47 && position < 56 && this.tab_position[position-16] == 0) {liste_coups.add(position*101-16);}
				//if(position > 7 && position < 16){}
				liste_coups.add(101*position-8);
				
			}
			
			if(position%8 != 7)
			{
				if(b){this.cases_controllees.add(position-7);}
				
				if(this.tab_position[position-7] > 0){
					//if(position > 7 && position < 16){}
				
					liste_coups.add(101*position-7);
				}
				
			}
			
			if(position%8 != 0)
			{
				if(b){this.cases_controllees.add(position-9);}
				
				if(this.tab_position[position-9] > 0){
					//if(position > 7 && position < 16){}

					liste_coups.add(101*position-9);
				}
				if(position-7 == this.passant_weak_square) {liste_coups.add(position*101-7);}
				if(position-9 == this.passant_weak_square) {liste_coups.add(position*101-9);}
				/////////////////
			}
			

			
		}
		
		return liste_coups;	
	}
	
	
		
	
	
	ArrayList<Integer> CoupsFou(int position, boolean b) {
		ArrayList<Integer> liste = new ArrayList<Integer>();
		int p = position-9;  //p est la case sur laquelle se trouve le fou. Les 4 boucles sont les 4 diagonales
		
		while(p%8 != 7 && p >= 0)	//bas gauche	
		{
			if(this.tab_position[p] != 0)
			{
				if(b){this.cases_controllees.add(p);} //pourquoi if(b) ??!
				if(this.tab_position[p]*this.tab_position[position] < 0) {liste.add(position*100+p);}
				break;
			}
			liste.add(100*position+p);
			if(b){this.cases_controllees.add(p);}
			p -= 9;
		}
		
		
		p = position-7;	////bas droite	
		while(p%8 != 0 && p >= 0)
		{
			if(this.tab_position[p] != 0)
			{
				if(b){this.cases_controllees.add(p);}
				if(this.tab_position[p]*this.tab_position[position] < 0) {liste.add(100*position+p);}
				break;
			}
			liste.add(100*position+p);
			if(b){this.cases_controllees.add(p);}
			p -= 7;
		}
		
		p = position+9;	////haut droite
		while(p%8 != 0 && p < 64)
		{
			if(this.tab_position[p] != 0)
			{
				if(b){this.cases_controllees.add(p);}
				if(this.tab_position[p]*this.tab_position[position] < 0) {liste.add(100*position+p);}
				break;
			}
			liste.add(100*position+p);
			if(b){this.cases_controllees.add(p);}
			p += 9;
		}
		
		p = position+7;	////haut gauche	
		while(p%8 != 7 && p < 64)
		{
			if(this.tab_position[p] != 0)
			{
				if(b){this.cases_controllees.add(p);}
				if(this.tab_position[p]*this.tab_position[position] < 0) {liste.add(100*position+p);}
				break;
			}
			liste.add(100*position+p);
			if(b){this.cases_controllees.add(p);}
			p += 7;
		}
		
		
		return liste;
	}
	
	
	ArrayList<Integer> CoupsTour(int position, boolean b) {
		ArrayList<Integer> liste = new ArrayList<Integer>();
		int p = position-1; //p est la case sur laquelle passe la tour
		while((p+1)%8 != 0) //à gauche, ici, il faut pas oublier que -1%8 = -1 en java
		{
			if(this.tab_position[p] != 0)
			{	
				if(b){this.cases_controllees.add(p);}
				if(this.tab_position[p]*this.tab_position[position] < 0) {liste.add(100*position+p);}
				break;
			}
			liste.add(100*position+p);
			if(b){this.cases_controllees.add(p);}
			p--;
		}
		p = position+1;
		while(p%8 != 0) // à droite
		{
			if(this.tab_position[p] != 0)
			{
				if(b){this.cases_controllees.add(p);}
				if(this.tab_position[p]*this.tab_position[position] < 0) {liste.add(100*position+p);}
				break;
			}
			liste.add(100*position+p);
			if(b){this.cases_controllees.add(p);}
			p++;
		}
		
		p = position+8;
		while(p < 64) // en haut
		{
			if(this.tab_position[p] != 0)
			{
				if(b){this.cases_controllees.add(p);}
				if(this.tab_position[p]*this.tab_position[position] < 0) {liste.add(100*position+p);}
				break;
			}
			liste.add(100*position+p);
			if(b){this.cases_controllees.add(p);}
			p+= 8;
		}
		
		p = position-8;
		while(p >= 0) // en bas
		{
			if(this.tab_position[p] != 0)
			{
				if(b){this.cases_controllees.add(p);}
				if(this.tab_position[p]*this.tab_position[position] < 0) {liste.add(100*position+p);}
				break;
			}
			liste.add(100*position+p);
			if(b){this.cases_controllees.add(p);}
			p-=8;
		}		
		return liste;
	}
	
	public ArrayList<Integer> CoupsDame(int position, boolean b)
	{
		ArrayList<Integer> c = new ArrayList<Integer>();
		c.addAll(CoupsTour(position, b));
		c.addAll(CoupsFou(position, b));
		return c;
	}
	
	
	ArrayList<Integer> CoupsRoi(int position, boolean b) {
		ArrayList<Integer> liste = new ArrayList<Integer>();
		int coef = this.tab_position[position]/9;
	
			if(position+7 < 64 && (position+7)%8 != 7){if(b){this.cases_controllees.add(position+7);} if(this.tab_position[position+7]*coef <= 0) {liste.add(101*position+7);}}
			if(position+8 < 64){if(b){this.cases_controllees.add(position+8);} if(this.tab_position[position+8]*coef <= 0) {liste.add(101*position+8);}}
			if(position+9 < 64 && (position+9)%8 != 0){if(b){this.cases_controllees.add(position+9);}  if(this.tab_position[position+9]*coef <= 0) {liste.add(101*position+9);}}
			if((position+1)%8 != 0){if(b){this.cases_controllees.add(position+1);} if(this.tab_position[position+1]*coef <= 0) {liste.add(101*position+1);}}
			if((position)%8 != 0){if(b){this.cases_controllees.add(position-1);}  if(this.tab_position[position-1]*coef <= 0) {liste.add(101*position-1);}}
			if(position-7 >= 0 && (position-7)%8 != 0){if(b){this.cases_controllees.add(position-7);}  if(this.tab_position[position-7]*coef <= 0) {liste.add(101*position-7);}}
			if(position-8 >= 0) {if(b){this.cases_controllees.add(position-8);}  if(this.tab_position[position-8]*coef <= 0) {liste.add(101*position-8);}}
			if(position-9 >= 0 && (position-9)%8 != 7){if(b){this.cases_controllees.add(position-9);}  if(this.tab_position[position-9]*coef <= 0) {liste.add(101*position-9);}}
			
			
			/////////////gestion du roque
			if(b)
			{
				Position p2 = this.SimulerCoup(0);	
			
				if(this.trait)
				{
					if(this.droits_roque[0] && this.tab_position[1] == 0 && this.tab_position[2] == 0 && this.tab_position[3] == 0 && !p2.isSquareControlled(4, false) && !p2.isSquareControlled(3, false)) {liste.add(402);}
					if(this.droits_roque[1] && this.tab_position[5] == 0 && this.tab_position[6] == 0 && !p2.isSquareControlled(5, false) && !p2.isSquareControlled(4,  false)) {liste.add(406);}
				}																																	
				//Si la case ou le roi arrive est controlée, pas de soucis, ça sera réglé avec tous les autres coups pseudo-légaux
				if(!this.trait)
				{

					if(this.droits_roque[2] && this.tab_position[57] == 0 && this.tab_position[58] == 0 && this.tab_position[59] == 0 && !p2.isSquareControlled(59, true) && !p2.isSquareControlled(60, true)) {liste.add(6058);}
					if(this.droits_roque[3] && this.tab_position[61] == 0 && this.tab_position[62] == 0 && !p2.isSquareControlled(61, true) && !p2.isSquareControlled(60, true)) {liste.add(6062);}
				}
			}
		
		
		
		return liste;
	}
	
	
	
	
	public boolean isSquareControlled(int s, boolean b) //teste, dans la position actuelle, si la case est controllée ou non par le camp indiqué par b
	{
		
		boolean Cava = false;
		boolean Fou = false;
		boolean Pion = false;
		boolean Dame = false;
		boolean Tour = false;

		if(b)
		{
			for(int i:this.tab_position)
			{
	
				if(i == 1){Pion = true;}
				if(i == 3){Cava = true;}
				if(i == 4){Fou = true;}
				if(i == 5){Tour = true;}
				if(i == 8){Dame = true;}
				
			}
			if(Cava)
			{
				for(int c: this.CoupsCava(s, false))
				{
					if(this.tab_position[c%100] == 3){return true;}
				}
			}
			if(Dame || Tour)
			{
				for(int c: this.CoupsTour(s, false))
				{
					if(this.tab_position[c%100] == 5 || this.tab_position[c%100] == 8)
					{
						return true;
					}
				}
			}
			if(Dame || Fou)
			{
				for(int c: this.CoupsFou(s, false))
				{
					if(this.tab_position[c%100] == 4 ||this.tab_position[c%100] == 8)
					{
						return true;
					}
				}
			}
			if(Pion)
			{
				if(s>15)
				{
					if(s%8 != 0)
					{
						if(this.tab_position[s-9] == 1){return true;}
					}
					if(s%8 != 7)
					{
						if(this.tab_position[s-7] == 1){return true;}
					}
				}
			}
			
			for(int c: this.CoupsRoi(s, false))
			{
				if(this.tab_position[c%100] == 9) {return true;}
			}
			return false;  //si rien n'a été detecté, on retourne false
		}
		else
		{
			for(int i:this.tab_position)
			{
				if(i == -1){Pion = true;}
				if(i == -3){Cava = true;}
				if(i == -4){Fou = true;}
				if(i == -5){Tour = true;}
				if(i == -8){Dame = true;}
				
			}
			if(Cava)
			{
				for(int c: this.CoupsCava(s, false))
				{
					if(this.tab_position[c%100] == -3){return true;}
				}
			}
			if(Dame || Tour)
			{
				for(int c: this.CoupsTour(s, false))
				{
					if(this.tab_position[c%100] == -5 || this.tab_position[c%100] == -8)
					{
						return true;
					}
				}
			}
			if(Dame || Fou)
			{
				for(int c: this.CoupsFou(s, false))
				{
					if(this.tab_position[c%100] == -4 || this.tab_position[c%100] == -8)
					{
						return true;
					}
				}
			}
			if(Pion)
			{
				if(s<48)
				{
					if(s%8 != 0)
					{
						if(this.tab_position[s+7] == -1){return true;}
					}
					if(s%8 != 7)
					{
						if(this.tab_position[s+9] == -1){return true;}
					}
				}
			}
			
			for(int c: this.CoupsRoi(s, false))
			{
				if(this.tab_position[c%100] == -9) {return true;}
			}
			return false;
		}
	}
	

	
	
	public int deltaMatos(int c) //trouve le bonus de matériel qu'apporte un coup, très rapide et utile pour certaines coupures
	{
		int deltaMatos = 0;
		if((this.tab_position[(int)(c/100)] == 1 && (int)(c/100) > 47))
		{
			deltaMatos += 850;
		}
		else if(this.tab_position[(int)(c/100)] == -1 && (int)(c/100) < 16)
		{
			deltaMatos -= 850;
		}
		deltaMatos -= ValPiece[this.tab_position[c%100]+8];
		return deltaMatos;
	}
	

	
	
	

	///fonction de hachage Zobrist. La table de départ est statique dans la classe Hachage
	public int hashCode()
	{
		int h=0;
		int i=0;
		while(i<64)
		{
			h = h ^ RootEngine.Z[i][this.tab_position[i]+9];
			i++;
		}
		if(this.passant_weak_square != -1) {h = h^RootEngine.Z[this.passant_weak_square][2];} //pour la prise en passant
		if(this.droits_roque[0]) {h = h^RootEngine.Z[0][3];} if(this.droits_roque[1]) {h = h^RootEngine.Z[1][3];}
		if(this.droits_roque[2]) {h = h^RootEngine.Z[2][3];} if(this.droits_roque[3]) {h = h^RootEngine.Z[3][3];}
		if(this.trait){h = h^RootEngine.Z[4][3];}
		return h;
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}