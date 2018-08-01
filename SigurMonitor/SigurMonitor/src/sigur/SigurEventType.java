package sigur;

import java.io.Serializable;

public enum SigurEventType implements Serializable{
    
	SUCCESS_ENTER("24", "������ ��������"),
    FAIL_FACE_SCAN("67", "������ ��������. ���� �� ��������"),
    FAIL_WRONG_CODE("10", "������ ��������. ����������� ��� ��������"),
    FAIL_EXPIRED("15", "������ ��������. ���� �������� ����� �����"),
    FAIL_TIME_LIMIT("13", "������ ��������. ��� ������� � ��� �����");

    private final String code;
    private final String description;

    SigurEventType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static final SigurEventType getByCode(String code){
        SigurEventType[] allEvents = SigurEventType.values();
        for(SigurEventType sigurEventType:allEvents){
            if(sigurEventType.code.equals(code)){
                return sigurEventType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.code;
    }

    public String getDescription() {
        return description;
    }
}