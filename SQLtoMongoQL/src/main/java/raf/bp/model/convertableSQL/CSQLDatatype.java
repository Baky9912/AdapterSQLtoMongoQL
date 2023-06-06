package raf.bp.model.convertableSQL;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract public class CSQLDatatype extends CSQLType {
    private Subtype subtype;
    public enum Subtype {
        STRING("String"),
        NUMBER("Number"),
        FIELD("Field"),
        SPECIAL("Special"),
        ARRAY("Array"),
        AGGREGATE_FUNC("Aggregate function");

        private Subtype(String subtype){
            this.subtype = subtype;
        }
        private String subtype;
        public String getSubtype(){
            return subtype;
        }
    }
}
