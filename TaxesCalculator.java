/**
 * Liferay Graduate Program - Taxes Calculator
 * This file includes the developed code to implement a taxes calculator complying with the guidelines explained
 * in the PDF document.
 *
 * USAGE: It is expected to provide a path to an input file containing the "primitive" ticket as an argument.
 * It must be a .txt file.
 */

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TaxesCalculator {

    /*
       A set of "dictionaries" is declared containing words related with exempt products. Some additional examples have
       been added to illustrate how the program could look up for more products.
     */
    private static final List<String> books = List.of("libro","cuaderno","novela","cuento");
    private static final List<String> healthcare = List.of("pastillas","jarabe","tiritas","supositorios","vendas","pomada");
    private static final List<String> foods = List.of("chocolate","bombones","naranjas","peras","manzanas","pollo","pan","ternera","atún");

    /**
     * The method checks each word related to the product and tries to find it in any of the dictionaries containing
     * exempt products. If so, it returns as true. If not, by default, the return value will be false
     * @param product A set of words describing the product bought
     * @return        A boolean indicating whether the product is exempt or not.
     */
    public static boolean checkExempt(String[] product){
        boolean exempt = false;
        for(String word: product){
            if(books.contains(word) || healthcare.contains(word) || foods.contains(word)){
                exempt = true;
                break;
            }
        }
        return exempt;
    }

    /**
     * The method will try to extract the words describing the product within a line based on the given structure
     * provided in the example.
     *
     * @param line  A set of words representing a complete line of input
     * @param imported  A boolean value indicating if the product has been imported or not
     * @return  A set of words describing the corresponding product
     */
    public static String[] extractProduct (String[] line, boolean imported){
        int end = 0;
        if(imported){
            end = line.length - 4;
        }else{
            end = line.length - 3;
        }
        return Arrays.copyOfRange(line, 1, end);
    }
    public static void main (String[] args){

        //Taxes values are declared as a percentage instead of an integer to ease calculations
        double salesTax = 0.1;
        double importTax = 0.05;

        Scanner input = null;
        double totalTaxes = 0;
        double totalPrice = 0;

        //If no path was indicated as an argument, exit the program
        if(args.length==0){
            System.out.println("ERROR: No se ha introducido ninguna ruta de archivo.");
            System.exit(-1);
        }
        //If the indicated file it's not of .txt type, exit the program
        if(!args[0].contains(".txt")){
            System.out.println("ERROR: El archivo indicado no es un archivo de texto.");
            System.exit(-1);
        }

        //Create a File object with the corresponding path and try to scan it. If any error is thrown, exit the program
        File path = new File(args[0]);
        try{
            input = new Scanner(path);
        }catch(FileNotFoundException e){
            System.out.println("ERROR: El archivo indicado no existe.");
            System.exit(-1);
        }catch(Exception e){
            System.out.println("ERROR: Ha ocurrido un error.");
            System.exit(-1);
        }

        //For each line of input
        while(input.hasNextLine()){
            String rawLine = input.nextLine();
            String[] line = rawLine.split(" "); /* Split a line in words */

            //Obtain the quantity of each product, and its price in mathematical notation ("." representing decimals)
            int quantity = Integer.parseInt(line[0]);
            String dottedPrice = line[line.length-2].replace(",",".");
            double priceUnit = Double.parseDouble(dottedPrice);

            //Obtain the word in the position where "imported" should be and check if it corresponds to it
            String importWord = line[line.length - 4];
            boolean imported = importWord.equals("importado") || importWord.equals("importados")
                                || importWord.equals("importada") || importWord.equals("importadas");
            //Determine of the product is tax exempt.
            String[] product = extractProduct(line, imported);
            boolean exempt = checkExempt(product);

            /*
            Based on the conditions obtained before, compute the corresponding taxes for the product with the expected
            rounding format
             */
            double taxes = 0;

            if(imported && !exempt){
                taxes = priceUnit*quantity*(importTax+salesTax);
            }else if(imported && exempt){
                taxes = priceUnit*quantity*importTax;
            }else if(!exempt){
                taxes = priceUnit*quantity*salesTax;
            }
            taxes = 0.05 * Math.round(taxes/0.05);
            //Add the final price and taxes to the total values
            totalPrice+= (priceUnit*quantity + taxes);
            totalTaxes+= taxes;
            //Determine the position corresponding to the end of the reusable part of the input string
            int slice = rawLine.lastIndexOf("a")-1;
            //Create the expected format for the output and print it
            String outputUnit = rawLine.substring(0,slice)+": "+ String.format("%.02f", (priceUnit*quantity+taxes)) + " €";
            System.out.println(outputUnit);

        }
        //Create the lines stating the taxes and total cost and print them
        String taxLine = "Impuestos sobre las ventas: "+String.format("%.02f", totalTaxes)+ " €";
        String totalLine = "Total: "+String.format("%.02f", totalPrice)+ " €";
        System.out.println(taxLine);
        System.out.println(totalLine);


    }
}
