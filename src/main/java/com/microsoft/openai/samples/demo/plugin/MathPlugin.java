package com.microsoft.openai.samples.demo.plugin;

import com.microsoft.semantickernel.skilldefinition.annotations.DefineSKFunction;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionInputAttribute;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionParameters;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathPlugin {

    @DefineSKFunction(description = "Adds amount to a value.", name = "add")
    public String add(
            @SKFunctionInputAttribute(description = "The value to add to.") String input,
            @SKFunctionParameters(name = "amount", description = "The amount to be added to value.")
                    String amount) {
        BigDecimal bValue = new BigDecimal(input);
        return bValue.add(new BigDecimal(amount)).toString();
    }

    @DefineSKFunction(description = "Subtracts amount from value.", name = "Subtract")
    public String subtract(
            @SKFunctionInputAttribute(description = "The value to subtract from.") String input,
            @SKFunctionParameters(name = "amount", description = "The amount to be subtracted from value.")
                    String amount) {
        BigDecimal bValue = new BigDecimal(input);
        return bValue.subtract(new BigDecimal(amount)).toString();
    }

    @DefineSKFunction(description = "Multiplies value by a factor.", name = "multiply")
    public String multiply(
            @SKFunctionInputAttribute(description = "The value to be multiplied.") String input,
            @SKFunctionParameters(name = "factor", description = "The factor to multiply the value by.")
                    String factor) {
        BigDecimal bValue = new BigDecimal(input);
        return bValue.multiply(new BigDecimal(factor)).toString();
    }

    @DefineSKFunction(description = "Divides value by a divisor.", name = "divide")
    public String divide(
            @SKFunctionInputAttribute(description = "The value to be divided.") String input,
            @SKFunctionParameters(name = "divisor", description = "The divisor to divide the value by.")
                    String divisor) {
        BigDecimal bValue = new BigDecimal(input);
        // Using RoundingMode.HALF_UP for demonstration. Adjust rounding mode as necessary.
        return bValue.divide(new BigDecimal(divisor), RoundingMode.HALF_UP).toString();
    }
}
