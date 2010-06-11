package cz.matfyz.sykora.sudoku.test;

import cz.matfyz.sykora.sudoku.*;

public class SolverTest {
	public static void main(String[] _args) {
		System.out.print("Solving...");
		long start_time = System.currentTimeMillis();
		Sudoku test = new Sudoku();
		
		// medium
/*		test.loadGameState(
				"x2x176xx5" +
				"xxx8xx1xx" +
				"51xx9xx82" +
				"xxxxxxx51" +
				"xxxxxxxxx" +
				"48xxxxxxx" +
				"63xx5xx19" +
				"xx4xx7xxx" +
				"7xx913x4x");*/
		// very hard
/*		test.loadGameState(
				"xx84xx35x" +
				"xxxxx1x8x" +
				"xx39x8xx6" +
				"2xxx9x7xx" +
				"x9xx6xx1x" +
				"xx5x1xxx3" +
				"6xx1x24xx" +
				"x4x6xxxxx" +
				"x89xx56xx"
		);*/
		// easy
/*		test.loadGameState(
				"xxx7x3xx2" +
				"187x92x35" +
				"x5x4x8x97" +
				"x34xxx9xx" +
				"7x5x3x1x6" +
				"xx2xxx37x" +
				"97x1x6x4x" +
				"32x94x781" +
				"5xx3x7xxx"
		);*/
/*		test.loadGameState(
				"xxx5x7xxx" +
				"x72x4x58x" +
				"x541x297x" +
				"2x54x87x9" +
				"x8xx2xx4x" +
				"4x96x38x2" +
				"x287x143x" +
				"x91x3x62x" +
				"xxx2x6xxx"
		);*/
/*		test.loadGameState(
				"x876x452x" +
				"xx6xxx8xx" +
				"1xxx8xxx3" +
				"96xx2xx51" +
				"xxx461xxx" +
				"xx1xxx4xx" +
				"x4x536x9x" +
				"69xx7xx34" +
				"81x9x2x65"
		);*/
/*		test.loadGameState(
				"xxx418xxx" +
				"xx6x2x1xx" +
				"x1x6x3x5x" +
				"8x4x6x3x9" +
				"17x349x85" +
				"9x3x8x4x2" +
				"x2x7x1x3x" +
				"xx1x3x7xx" +
				"xxx256xxx"
		);*/
/*		test.loadGameState(
				"5xx17xx39" +
				"xx79xxxxx" +
				"x1xxxx4xx" +
				"xxx8x27xx" +
				"3x8xxx6x2" +
				"xx54x6xxx" +
				"xx2xxxx5x" +
				"xxxxx59xx" +
				"15xx29xx7"
		);*/
		/*test.loadGameState(
				"x43x8x25x" +
				"6xxxxxxxx" +
				"xxxxx1x94" +
				"9xxxx4x7x" +
				"xxx6x8xxx" +
				"x1x2xxxx3" +
				"82x5xxxxx" +
				"xxxxxxxx5" +
				"x34x9x71x");*/
		/*test.loadGameState(
				"xxxxxxx1x" +
				"4xxxxxxxx" +
				"x2xxxxxxx" +
				"xxxx5x4x7" +
				"xx8xxx3xx" +
				"xx1x9xxxx" +
				"3xx4xx2xx" +
				"x5x1xxxxx" +
				"xxx8x6xxx"
				);*/
		/*test.loadGameState(
				"4xxxxx8x5" +
				"x3xxxxxxx" +
				"xxx7xxxxx" +
				"x2xxxxx9x" +
				"xxxx8x4xx" +
				"xxxx1xxxx" +
				"xxx6x3x7x" +
				"5xx2xxxxx" +
				"1x4xxxxxx"
				);*/

		boolean result = test.solve(); 
		long end_time = System.currentTimeMillis();
		if(result)
			System.out.println("Succeeded");
		else
			System.out.println("Failed");
		test.debugOutput();
		System.out.println("Total time: " + (end_time - start_time) + " milliseconds");
	}
}
