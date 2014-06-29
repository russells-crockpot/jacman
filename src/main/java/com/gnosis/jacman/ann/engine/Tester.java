package com.gnosis.jacman.ann.engine;

import com.gnosis.jacman.engine.Globals;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 25; i++){
			System.out.println(.5 - Globals.RNG.nextDouble());
		}
	}

}
