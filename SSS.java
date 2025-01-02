
import org.json.JSONObject;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Main{

    private static BigInteger decodeValue(String base, String value) {
        int baseNumber = Integer.parseInt(base);
        return new BigInteger(value, baseNumber);
    }


    private static BigInteger findConstantTerm(List<BigInteger[]> points, int requiredRoots) {
        BigInteger secret = BigInteger.ZERO;
        int modulus = 256; 
        for (int i = 0; i < requiredRoots; i++) {
            BigInteger[] currentPoint = points.get(i);
            BigInteger x_i = currentPoint[0];
            BigInteger y_i = currentPoint[1];

            BigInteger lagrangeNumerator = BigInteger.ONE;
            BigInteger lagrangeDenominator = BigInteger.ONE;

            for (int j = 0; j < requiredRoots; j++) {
                if (i != j) {
                    BigInteger[] comparisonPoint = points.get(j);
                    BigInteger x_j = comparisonPoint[0];

                    lagrangeNumerator = lagrangeNumerator.multiply(x_j.negate()); // Numerator part (0 - x_j)
                    lagrangeDenominator = lagrangeDenominator.multiply(x_i.subtract(x_j)); // Denominator part (x_i - x_j)
                }
            }

            BigInteger lagrangeTerm = y_i.multiply(lagrangeNumerator).divide(lagrangeDenominator);
            secret = secret.add(lagrangeTerm);
        }

    
        return secret.mod(BigInteger.valueOf(2).pow(modulus));
    }

    public static void main(String[] args) throws Exception {
        
        JSONObject jsonInput = new JSONObject(new FileReader("input.json"));
        JSONObject keys = jsonInput.getJSONObject("keys");

        int totalRoots = keys.getInt("n");
        int requiredRoots = keys.getInt("k"); 

        List<BigInteger[]> points = new ArrayList<>();

        for (int i = 1; i <= totalRoots; i++) {
            if (jsonInput.has(String.valueOf(i))) {
                JSONObject point = jsonInput.getJSONObject(String.valueOf(i));
                String base = point.getString("base");
                String value = point.getString("value");

                BigInteger x_value = BigInteger.valueOf(i);  
                BigInteger y_value = decodeValue(base, value); 

                points.add(new BigInteger[]{x_value, y_value}); 
            }
        }

        BigInteger secretConstant = findConstantTerm(points, requiredRoots);

        System.out.println("The secret constant term 'c' is: " + secretConstant);
    }
}
