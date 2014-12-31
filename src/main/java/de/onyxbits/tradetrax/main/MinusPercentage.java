package de.onyxbits.tradetrax.main;

import net.objecthunter.exp4j.operator.Operator;

/**
 * A Custom exp4j operator for subtracting a percentage of a value from a value
 * (e.g. to calculate a transaction fee).
 * 
 * @author patrick
 * 
 */
public class MinusPercentage extends Operator {

	public MinusPercentage() {
		super("-%", 2, true, Operator.PRECEDENCE_ADDITION - 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double apply(double... arg) {
		return arg[0] - arg[0] * arg[1] / 100;
	}

}
