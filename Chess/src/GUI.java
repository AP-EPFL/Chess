import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUI extends JFrame {
	
	Position CurrentPos;
	int Coup; //le coup entrain d'être joué
	JLabel[] Liste_Cases = new JLabel[64];
	RootEngine R;
	boolean TourOrdi = true;
	
	JPanel ContentPane;
	JPanel BoardPane;
	JLabel Depth;
	JLabel BestVar;
	JLabel Eval;
	JLabel T;
	
	public GUI(Position p) //étape 1: instancier tout ce dont on a besoin
	{
		this.ContentPane = new JPanel();
		this.BoardPane = new JPanel();
		GridLayout BoardLayout = new GridLayout(8,8);
		this.BoardPane.setLayout(BoardLayout);
		
		JSplitPane MainSplit = new JSplitPane();
		
		JPanel OtherSidePane = new JPanel();
		GridLayout OtherSideLayout = new GridLayout(4,1);
		JPanel FirstLine = new JPanel();
		
		JButton NewGame = new JButton("New Game");
		//JButton EditPos = new JButton("Edit Pos");
		
		this.Depth = new JLabel();
		
		this.T = new JLabel();
		
		JPanel VarEval = new JPanel();
		this.Eval = new JLabel();
		this.BestVar = new JLabel();
		
		int i = 0;
		while(i<64)
		{
			JLabel s = new JLabel(new ImageIcon("GIF/b.gif"));
			s.addMouseListener(new SquareListener(i));
			this.Liste_Cases[i] = s;
			this.BoardPane.add(s);
			i++;
		}
		
		
		//étape 2: donner les bonnes caractéristiques aux éléments.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setSize(500, 400);
		this.ContentPane.setSize(464,264);
		this.BoardPane.setSize(264,264);
		OtherSidePane.setSize(200, 264);
		
		NewGame.setSize(100, 66);

		NewGame.addActionListener(new NewGameButtonListener());

		
		
		
		
		
		//étape 3: imbriquer tout les éléments
		this.setContentPane(ContentPane);
		ContentPane.add(BoardPane, BorderLayout.WEST);
		ContentPane.add(OtherSidePane, BorderLayout.EAST);
		
		///OtherSidePane
		OtherSidePane.setLayout(OtherSideLayout);
		
		OtherSidePane.add(FirstLine);
		FirstLine.add(NewGame, BorderLayout.WEST);

		
		OtherSidePane.add(Depth);
		
		OtherSidePane.add(this.T);
		
		OtherSidePane.add(VarEval);
		VarEval.add(Eval, BorderLayout.WEST);
		VarEval.add(BestVar, BorderLayout.EAST);
		
		
		
		this.SetCurrentPos(p);
		
		
		this.setVisible(true);
		
		if(this.TourOrdi)
		{
			think();
		}
		
	}
	public void SetCurrentPos(Position p)  //modifie la position actuelle. Toutes les mises à jour graphiques sont faites ICI
	{
		this.BoardPane.removeAll();
		this.CurrentPos = p;
		this.CurrentPos.coups_legaux();
		
		int[] newTabPos = new int[64]; //on met la position dans la bon ordre pour remplir la grille
		int i = 0;
		while(i<64)
		{
			newTabPos[i] = this.CurrentPos.tab_position[i+56-(16*((int)i/8))];   
			i++;		
		}
		
		
		i = 0;
		 //boucle sur les pièces
		while(i<64)
		{
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == 0)
			{
				//case noire vide
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/b.gif")));
				
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == 0)
			{
				//case blanche vide
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/w.gif")));
				
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == 1)
			{
				//case noire pion blanc
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wpb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == 1)
			{
				//case blanc pion blanc
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wpw.gif")));
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == -1)
			{			
				//case noire pion noir
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/bpb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == -1)
			{
				//case noire pion blanc
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/bpw.gif")));
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == 3)
			{
				//etc...
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wnb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == 3)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wnw.gif")));
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == -3)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/bnb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == -3)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/bnw.gif")));
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == 4)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wbb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == 4)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wbw.gif")));
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == -4)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/bbb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == -4)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/bbw.gif")));
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == 5)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wrb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == 5)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wrw.gif")));
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == -5)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/brb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == -5)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/brw.gif")));
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == 8)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wqb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == 8)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wqw.gif")));
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == -8)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/bqb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == -8)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/bqw.gif")));
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == 9)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wkb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == 9)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/wkw.gif")));
			}
			if((i%8-((int)i/8))%2 != 0 && newTabPos[i] == -9)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/bkb.gif")));
			}
			if((i%8-((int)i/8))%2 == 0 && newTabPos[i] == -9)
			{
				this.Liste_Cases[i].setIcon(new ImageIcon(getClass().getResource("/GIF/bkw.gif")));
			}
			this.BoardPane.add(this.Liste_Cases[i]);
			i++;
		}
		BoardPane.repaint();
		BoardPane.revalidate();

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//////////////////////////////////////////////////////quelques classes internes utiles	
	
	
	
	
	class SquareListener implements MouseListener{
	
		
		int c; //La case, mais selon l'ordre de la grille! (Début du numérotage en haut à gauche)
		
		public SquareListener(int c)
		{	
			this.c=c;
			
		}
		@Override
		public void mouseClicked(MouseEvent arg0) { //c'est la seule fonction qui nous intéresse


		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			
			Coup = Coup-Coup%100;
			int n = this.c+56-(16*((int)this.c/8)); //n est la case selon la vraie numérotation
			Coup = Coup + n;
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			
			int n = this.c+56-(16*((int)this.c/8)); //n est la case selon la vraie numérotation
			Coup = n*100;
			
		}
		@Override
		
		public void mouseReleased(MouseEvent arg0) {
			
			for(int co: CurrentPos.liste_coups_legaux){
				
				if(Coup == co)
				{	
					SetCurrentPos(CurrentPos.SimulerCoup(Coup));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					think();						
				}
			}
			Coup = 0;
		}		
	}

	
	public void think() ///////demande à l'ordinateur de réfléchir
	{


		
		System.out.println("Début de la reflexion");
		this.R = new RootEngine();
		long t1 = System.nanoTime();
		int Val = this.R.IterativeDeepening(CurrentPos, 1);
		this.SetCurrentPos(CurrentPos.SimulerCoup(R.TranspoTable.get(CurrentPos.hashCode())[2]));  //on simule le coup de la première position fille
		long t2 = System.nanoTime();
		this.T.setText("  T[ms]: "+Integer.toString((int)((t2-t1)/1000000)));
		this.Eval.setText(" Eval[cp]: "+Integer.toString(Val));
		this.Depth.setText(" d = " +Integer.toString(R.TranspoTable.get(CurrentPos.hashCode())[1]+1));
		System.out.println("Fin!");
		R.TranspoTable.clear();
		this.TourOrdi = false;
		
	}	
	
	class NewGameButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
		int[] pos_base = {5,3,4,8,9,4,3,5,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,-1,-1,-1,-1,-1,-1,-1,-5,-3,-4,-8,-9,-4,-3,-5};
		boolean[] roque_base = {true, true, true, true};
		Position PosBase = new Position(pos_base, roque_base, true);
		SetCurrentPos(PosBase);
		
		}
		
	}

	
	
}


