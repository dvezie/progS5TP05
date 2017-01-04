import types.ABinHuffman;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;
import outilsArbre.OutilsArbre;
import outilsHuffman.OutilsHuffman;

/**
 * Réalisation du décodage d'un texte par la méthode de Huffman
 */

public class DecodageHuffman
{
  public static void main (String[] args)
  {
    //------------------------------------------------------------------------
    // 0. Saisir le nom du fichier à  décoder (À FAIRE)
    //------------------------------------------------------------------------
    String nomFichier;
    
    Scanner input=new Scanner(System.in);
    System.out.println("Entrer le nom du fichier à décoder : ");
    nomFichier=input.nextLine();
    input.close();

    //------------------------------------------------------------------------
    // 1. Lire et construire la table de fréquences (DONNÉ)
    //------------------------------------------------------------------------
    int [] tableFrequences = OutilsHuffman.lireTableFrequences(nomFichier);

    //------------------------------------------------------------------------
    // 2. Construire l'arbre de Huffman (DONNÉ)
    //------------------------------------------------------------------------
    ABinHuffman arbreHuffman =
      //OutilsHuffman.construireArbreHuffman(tableFrequences);
      CodageHuffman.construireArbreHuffman(tableFrequences);

    //------------------------------------------------------------------------
    // 2.1 afficher l'arbre de Huffman (À FAIRE)
    //------------------------------------------------------------------------
    afficherHuffman(arbreHuffman);

    //------------------------------------------------------------------------
    // 3. Lire le texte codé (DONNÉ)
    //------------------------------------------------------------------------
    String texteCode = OutilsHuffman.lireTexteCode(nomFichier);

    //------------------------------------------------------------------------
    // 4. Décoder le texte (À FAIRE)
    //------------------------------------------------------------------------
    StringBuilder texteDecode = decoderTexte(texteCode, arbreHuffman);

    //------------------------------------------------------------------------
    // 5. Enregistrer le texte décode (DONNÉ)
    //------------------------------------------------------------------------
    System.out.println("\nTexte décodé:\n\n" + texteDecode);
    enregistrerTexte(texteDecode, nomFichier + ".decode");
  }

  /**
   * 4. décoder une chaîne (non vide) encodée par le codage de Huffman
   * @param texteCode    : chaîne de "0/1" à  décoder
   * @param arbreHuffman : arbre de (dé)codage des caractères
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
   * 6. Enregistre le texte décodé dans le fichier indiqué
   * @param texte : texte décodé
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
