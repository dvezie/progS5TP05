import types.ABinHuffman;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;
import outilsArbre.OutilsArbre;
import outilsHuffman.OutilsHuffman;

/**
 * R�alisation du d�codage d'un texte par la m�thode de Huffman
 */

public class DecodageHuffman
{
  public static void main (String[] args)
  {
    //------------------------------------------------------------------------
    // 0. Saisir le nom du fichier � d�coder (� FAIRE)
    //------------------------------------------------------------------------
    String nomFichier;
    
    Scanner input=new Scanner(System.in);
    System.out.println("Entrer le nom du fichier � d�coder : ");
    nomFichier=input.nextLine();
    input.close();

    //------------------------------------------------------------------------
    // 1. Lire et construire la table de fr�quences (DONN�)
    //------------------------------------------------------------------------
    int [] tableFrequences = OutilsHuffman.lireTableFrequences(nomFichier);

    //------------------------------------------------------------------------
    // 2. Construire l'arbre de Huffman (DONN�)
    //------------------------------------------------------------------------
    ABinHuffman arbreHuffman =
      //OutilsHuffman.construireArbreHuffman(tableFrequences);
      CodageHuffman.construireArbreHuffman(tableFrequences);

    //------------------------------------------------------------------------
    // 2.1 afficher l'arbre de Huffman (� FAIRE)
    //------------------------------------------------------------------------
    afficherHuffman(arbreHuffman);

    //------------------------------------------------------------------------
    // 3. Lire le texte cod� (DONN�)
    //------------------------------------------------------------------------
    String texteCode = OutilsHuffman.lireTexteCode(nomFichier);

    //------------------------------------------------------------------------
    // 4. D�coder le texte (� FAIRE)
    //------------------------------------------------------------------------
    StringBuilder texteDecode = decoderTexte(texteCode, arbreHuffman);

    //------------------------------------------------------------------------
    // 5. Enregistrer le texte d�code (DONN�)
    //------------------------------------------------------------------------
    System.out.println("\nTexte d�cod�:\n\n" + texteDecode);
    enregistrerTexte(texteDecode, nomFichier + ".decode");
  }

  /**
   * 4. d�coder une cha�ne (non vide) encod�e par le codage de Huffman
   * @param texteCode    : cha�ne de "0/1" � d�coder
   * @param arbreHuffman : arbre de (d�)codage des caract�res
   */
  public static StringBuilder decoderTexte(String texteCode, ABinHuffman arbreHuffman)
  {
	  StringBuilder texteDecode=new StringBuilder();
	  ABinHuffman abh=arbreHuffman;
	  for(int i=0;i<texteCode.length();i++){
		  if(texteCode.charAt(i)=='0'){
			  abh=abh.filsGauche();
		  }else{
			  abh=abh.filsDroit();
		  }
		  if(abh.estFeuille()){
			  texteDecode.append(abh.getValeur().premier());
			  abh=arbreHuffman;
		  }
	  }
	  return texteDecode;
  }

  /**
   * 2.1 afficher un arbre de Huffman
   * @param a : arbre binaire de Huffman
   */
  public static void afficherHuffman(ABinHuffman a)
  {
	  String titre="Arbre binaire de Huffman correspondant : ";
	  OutilsArbre.afficher(a, titre);
  }
  
  /**
   * 6. Enregistre le texte d�cod� dans le fichier indiqu�
   * @param texte : texte d�cod�
   * @param nomFichierDecode : fichier dans lequel enregistrer le texte
   */
  public static void enregistrerTexte ( StringBuilder texte , String nomFichierDecode) {
	  
	  OutputStream outputStream;
	  Writer outputStreamWriter;
	  
	  try {
		  outputStream = new FileOutputStream(nomFichierDecode);
		  outputStreamWriter = new OutputStreamWriter(outputStream, "ISO-8859-1");
		  outputStreamWriter.write(texte.toString());
		  outputStreamWriter.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
  }
} // DecodageHuffman
