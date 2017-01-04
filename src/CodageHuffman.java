import types.ABinHuffman;
import types.ListeABH;
import types.Couple;

import java.util.ListIterator;
import java.util.Scanner;

import outilsArbre.OutilsArbre;
import outilsHuffman.OutilsHuffman;

/**
 * Réalisation du codage d'un texte par la méthode de Huffman
 */

public class CodageHuffman
{
  public static void main (String[] args)
  {	
    //------------------------------------------------------------------------
    // 0. Saisir le nom du fichier à coder (A FAIRE)
    //------------------------------------------------------------------------
    String nomFichier;
    Scanner input=new Scanner(System.in);
    System.out.println("Entrer le nom du fichier à coder : ");
    nomFichier=input.nextLine();
    input.close();
    
    long tempsTotal = OutilsHuffman.getInstantPresent();
    
    //------------------------------------------------------------------------
    // 1. Lire le texte (DONNE)
    //------------------------------------------------------------------------
    char [] texte = OutilsHuffman.lireFichier(nomFichier);
    
    double tailleTextNonCode = texte.length;

    //------------------------------------------------------------------------
    // 2. Calculer la table des fréquences des caractères (A FAIRE)
    //------------------------------------------------------------------------
    int [] tableFrequences = calculerFrequences(texte);

    //------------------------------------------------------------------------
    // 3. Enregistrer la table de fréquences dans le fichier de sortie (DONNE)
    //------------------------------------------------------------------------
    OutilsHuffman.enregistrerTableFrequences(tableFrequences, nomFichier + ".code");

    //------------------------------------------------------------------------
    // 4. Construire l'arbre de codage de Huffman (DONNE - A FAIRE)
    //------------------------------------------------------------------------
    ABinHuffman arbreCodageHuffman = construireArbreHuffman(tableFrequences);

    //------------------------------------------------------------------------
    // Afficher l'arbre de codage de Huffman (DEJA FAIT)
    //------------------------------------------------------------------------
    System.out.println("Arbre de Huffman associé au texte " + nomFichier);
    DecodageHuffman.afficherHuffman(arbreCodageHuffman);

    //------------------------------------------------------------------------
    // 5. Construire la table de codage associée (A FAIRE)
    //------------------------------------------------------------------------
    String [] tablecodage = construireTableCodage(arbreCodageHuffman);

    //------------------------------------------------------------------------
    // 5.1. afficher la table de codage (A FAIRE)
    //------------------------------------------------------------------------
    System.out.println("Table de codage associée au texte " + nomFichier);
    afficherTableCodage(tablecodage);

    //------------------------------------------------------------------------
    // 6. coder le texte avec l'arbre de Huffman (A FAIRE)
    //------------------------------------------------------------------------
    long tempsCodage = OutilsHuffman.getInstantPresent();
    
    StringBuilder texteCode = coderTexte(texte, tablecodage);
    
    tempsCodage = OutilsHuffman.getInstantPresent()-tempsCodage;
    
    double octet = 8;
    double tailleTextCode = Math.ceil(texteCode.length()/octet);
    

    //------------------------------------------------------------------------
    // 7. enregistrer le texte codé (DONNE)
    //------------------------------------------------------------------------
    OutilsHuffman.enregistrerTexteCode(texteCode, nomFichier + ".code");

    tempsTotal = OutilsHuffman.getInstantPresent()-tempsTotal;
    //------------------------------------------------------------------------
    // xx. calculer et afficher les stats (A FAIRE)
    //------------------------------------------------------------------------
    // calculer la taille du fichier non codé   
    long tailleFichierNonCode = OutilsHuffman.tailleFichier(nomFichier);
    
    // calculer la taille du fichier codé    
    long tailleFichierCode = OutilsHuffman.tailleFichier(nomFichier + ".code");    
    
    //------------------------------------------------------------------------
    // Affichage des données concernant l'opération :
    //------------------------------------------------------------------------
    System.out.println("Taille texte   compressé: "+(int) tailleTextCode);
    System.out.println("Taux compression texte  : "+(int) (100.0*tailleTextCode/tailleTextNonCode)+" %");
    System.out.println("Taille fichier compressé: "+(int) tailleFichierCode);
    System.out.println("Taux compression fichier: "+(int) (100.0*tailleFichierCode/tailleFichierNonCode)+" %");
    System.out.println("Durée codage            : "+tempsCodage+" ms");
    System.out.println("Durée totale            : "+tempsTotal+" ms");
    
  }

  /**
   * 2. calculer la fréquence d'apparition de chaque caractère
   * @param  tcar tableau des caractères du texte
   * @return tableau de fréquence des caractères, indicé par les caractères
   */
  public static int [] calculerFrequences(char [] tcar)
  {
	  int[] tint = new int[256];
	  for(int i=0;i<tcar.length;i++){
		  tint[(int) tcar[i]]++;
	  }
	  return tint;
  }

  /**
   * 4. construire un arbre de codage de Huffman par sélection et combinaison
   * des éléments minimaux
   * @param tableFrequences table des fréquences des caractères
   * @return arbre de codage de Huffman
   */
  public static ABinHuffman construireArbreHuffman(int [] tableFrequences)
  {
	  ListeABH listeABinHuffman = faireListeAbinHuffman(tableFrequences);
	  while(listeABinHuffman.size()>1){
		  ListIterator<ABinHuffman> it = listeABinHuffman.listIterator();
		  ABinHuffman abh1 = it.next();
		  it.remove();
		  ABinHuffman abh2 = it.next();
		  it.remove();
		  ABinHuffman newABH = createABH((int) '.', abh1.getValeur().deuxieme()+abh2.getValeur().deuxieme());
		  newABH.setGauche(abh1);
		  newABH.setDroit(abh2);
		  addABHToList(listeABinHuffman, newABH);
	  }
	  return listeABinHuffman.getFirst();
  }

  /**
   * 4.1 Faire une liste triée dont chaque élément est un arbreBinaire<br>
   * comprenant un unique sommet dont l'étiquette est un couple
   * <caractère, fréquence>, trié par fréquence croissante
   * @param tableFrequences : table des fréquences des caractères
   * @return		      la liste triée
   */
  private static ListeABH faireListeAbinHuffman(int [] tableFrequences)
  {
	  ListeABH listeABinHuffman = new ListeABH();
	  int i=0;
	  while(i<tableFrequences.length){
		  addABHToList(listeABinHuffman, i, tableFrequences[i]);
		  i++;
	  }
	  return listeABinHuffman;  
  } 
  
  /**
   * Ajouter un arbre binaire de huffman dans une liste
   * @param listeABinHuffman : la liste
   * @param abh : l'arbre
   */
  private static void addABHToList(ListeABH listeABinHuffman, ABinHuffman abh)
  {
	  // Si la liste est vide, on ajoute directement notre Arbre.
	  if(listeABinHuffman.isEmpty()){		  
		  listeABinHuffman.add(abh);
	  }else{
	  ListIterator<ABinHuffman> it = listeABinHuffman.listIterator();			  
		  while(it.hasNext()){
			  // Si on trouve un nombre d'itération supérieur à celui de notre Arbre, 
			  // on recule l'itérateur et on insère notre Arbre.
			  if(it.next().getValeur().deuxieme()>abh.getValeur().deuxieme()){					  
				  it.previous();
				  it.add(abh);
				  break;
			  }
			  // Si on ne trouve pas de place pour notre arbre et que nous arrivons à la fin de la liste,
			  // on insère notre Arbre en fin de liste.
			  if(!it.hasNext()){
				  it.add(abh);
				  break;
			  }
		  }
	  }	  
  }
  
  /**
   * Ajouter un arbre binaire de huffman dans une liste
   * dans le cas où l'on nous donne uniquement le couple de l'arbre
   * @param listeABinHuffman : la liste
   * @param idCar : l'identifiant du caractère
   * @param nbItCar : nombre d'apparition de ce caractère dans le texte
   */
  private static void addABHToList(ListeABH listeABinHuffman, int idCar, int nbItCar){
	  if(nbItCar>0){
		  ABinHuffman abh = createABH(idCar, nbItCar);
		  addABHToList(listeABinHuffman, abh);
	  }
  }
  
  /**
   * Creer un arbre binaire de huffman à partir d'un couple
   * @param idCar : l'identifiant du caractère
   * @param nbItCar : nombre d'apparition de ce caractère dans le texte
   * @return	l'arbre binaire de huffman correspondant
   */
  private static ABinHuffman createABH(int idCar, int nbItCar){
	  Character c = new Character((char) idCar);
	  Integer i = new Integer(nbItCar);
	  Couple<Character,Integer> cp = new Couple<Character,Integer>(c,i);
	  ABinHuffman ab = new ABinHuffman();
	  ab.setValeur(cp);
	  return ab;
  }

  /**
   * 5. construire la table de codage correspondant à l'arbre de Huffman
   * @param abinHuff : arbre de Huffman
   * @return table de (dé)codage indicé par lex caractères
   */
  public static String [] construireTableCodage(ABinHuffman abinHuff)
  {
	  String[] tabCod = new String[256];
	  String codeNull = "null";
	  for(int i=0;i<tabCod.length;i++){
		  tabCod[i]=codeNull;
	  }
	  StringBuilder code = new StringBuilder();
	  completeTabCod(abinHuff, tabCod, code);
	  return tabCod;
  }
  
  /**
   * Completer les informations de la table de codage
   * à partir de l'arbre binaire de huffman correspondant
   * @param abinHuff : arbre de Huffman
   * @param tabCod : table de codage
   * @param code : code du caractère dans l'arbre 
   */
  private static void completeTabCod(ABinHuffman abinHuff, String[] tabCod, StringBuilder code){
	  if(abinHuff.estFeuille()){
		  tabCod[(int) abinHuff.getValeur().premier()] = code.toString();
	  }else{
		  StringBuilder codeG = new StringBuilder(code.toString());
		  StringBuilder codeD = new StringBuilder(code.toString());
		  codeG.append('0');
		  codeD.append('1');
		  completeTabCod(abinHuff.filsGauche(), tabCod, codeG);
		  completeTabCod(abinHuff.filsDroit(), tabCod, codeD);
	  }
  }

  /**
   * 5.1. Afficher la table de codage associée au texte
   * @param tablecodage : table de codage associée au texte
   */
  public static void afficherTableCodage(String [] tablecodage)
  {
	  for(int i=1;i<tablecodage.length;i++){
		  String code = tablecodage[i];		  
		  if(code!="null"){
			  switch(i){
			  case 10 :
				  System.out.println("<\\n> : "+code);
				  break;
			  case 13 :
				  System.out.println("<\\r> : "+code);
				  break;
			  case 32 : 
				  System.out.println("<space> : "+code);
				  break;
			  default :
				  System.out.println("<"+(char) i+"> : "+code);
				  break;
			  }
		  }	  
	  }
  }

  /**
   * 6. Coder un texte à l'aide de la table de codage
   * @param texte à coder
   * @param tablecodage : table de codage associée au texte
   * @return texte codé
   */
  public static StringBuilder coderTexte(char [] texte, String [] tablecodage)
  {
	  StringBuilder texteCode = new StringBuilder();
	  for(int i=0;i<texte.length;i++){
		  int idCar = (int) texte[i];
		  texteCode.append(tablecodage[idCar]);
	  }
	  return texteCode;
  }

}// CodageHuffman
