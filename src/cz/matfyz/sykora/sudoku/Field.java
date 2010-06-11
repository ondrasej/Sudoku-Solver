package cz.matfyz.sykora.sudoku;

/**
 * Reprezentace jednoho políčka v hracím plánu. Udržuje si
 * informace o použité hodnotě nebo o hodnotách přípustných
 * pro toto pole.
 * @author Ondra Sýkora [ondrasej@centrum.cz]
 */
public class Field {
	/**
	 * Nastaveno na true pokud je pro toto pole určena
	 * pevná hodnota.
	 * @see #assignedValue
	 * @see #clearValue()
	 * @see #hasFixedValue()
	 * @see #setValue(int)
	 */
	private boolean valueFixed;
	/**
	 * Hodnta přiřazená tomuto poli. Tato hodnota má smysl
	 * jen když je <i>hasFixedValue</i> nastaveno na true.
	 * @see #valueFixed
	 * @see #getValue()
	 * @see #setValue(int)
	 */
	private int assignedValue;
	/**
	 * Bitové pole s jedničkami na pozicích, které reprezentují
	 * číslice přípustné pro toto pole. Je reprezentováno jako
	 * jeden int, tím je dáno omezení na maximálně 32 hodnot.
	 * Tato hodnota má smysl pouze pokud je <i>hasFixedValue</i>
	 * nastaveno na false.
	 * @see #valueFixed
	 * @see #clearPossibleValue(int)
	 */
	private int possibleValues;
	
	public final void assign(Field _source) {
		valueFixed = _source.valueFixed;
		assignedValue = _source.assignedValue;
		possibleValues = _source.possibleValues;
	}
	
	/**
	 * Použitelné pouze pokud je pro toto pole přípustná jediná
	 * hodnota. Pak najde tuto hodnotu a přiřadí ji tomuto poli.
	 */
	public final void assignSinglePossibleValue() {
		int pos_val = possibleValues;
		int val = 1;
		while(pos_val != 0 && ((pos_val & 1) == 0)) {
			pos_val >>= 1;
			val++;
		}
		assignedValue = val;
		valueFixed = true;
	}
	
	public final void clearPossibleValue(int _value) {
		possibleValues &= ~(1 << (_value - 1));
	}
	
	public final void clearValue() {
		valueFixed = false;
		possibleValues = (1 << Sudoku.GAME_SIZE) - 1;
	}
	
	public final int getValue() {
		return assignedValue;
	}
	
	public final boolean hasFixedValue() {
		return valueFixed;
	}
	
	public final boolean hasPossibleValues() {
		return possibleValues != 0;
	}
	
	public final boolean hasSinglePossibleValue() {
		int pos_val = possibleValues;
		while(pos_val != 0 && ((pos_val & 1) == 0))
			pos_val >>= 1;
		return pos_val == 1;
	}
	
	public final boolean isPossibleValue(int _value) {
		int mask = 1 << (_value - 1);
		return 0 != (possibleValues & mask);
	}
	
	public final void setValue(int _value) {
		valueFixed = true;
		assignedValue = _value;
	}
	
	public Field() {
		clearValue();
	}
	
	public Field(Field _source) {
		assign(_source);
	}
	
	public Field(int _value) {
		valueFixed = true;
		assignedValue = _value;
	}
}
