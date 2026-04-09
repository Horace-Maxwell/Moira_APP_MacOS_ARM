package org.athomeprojects.swisseph;

public class SwissephException extends RuntimeException {
  private int type=0;
  private int rc=0;

  public static final int UNDEFINED                     = 0;

  // FILE errors:
  public static final int FILE_ERROR                    = 1;
  public static final int UNSPECIFIED_FILE_ERROR        = FILE_ERROR | 2;
  public static final int INVALID_FILE_NAME             = FILE_ERROR | 4;
  public static final int FILE_NOT_FOUND                = FILE_ERROR | 8;
  public static final int FILE_OPEN_FAILED              = FILE_ERROR | 16;
  public static final int FILE_READ_ERROR               = FILE_ERROR | 32;
  public static final int DATA_FILE_ERROR               = FILE_ERROR | 64;

  // Parameter errors:
  public static final int PARAM_ERROR                   = 128;
  public static final int OUT_OF_TIME_RANGE             = PARAM_ERROR | 256;
  public static final int UNSUPPORTED_OBJECT            = PARAM_ERROR | 512;
  public static final int INVALID_PARAMETER_COMBINATION = PARAM_ERROR | 1024;


  public SwissephException(int type, int rc, StringBuffer sb) {
    super(sb==null?null:sb.toString());
    this.type = type;
    this.rc = rc;
  }

  public SwissephException(int type, int rc, String s) {
    super(s);
    this.type = type;
    this.rc = rc;
  }

  public SwissephException(int type, String s) {
    super(s);
    this.type = type;
  }

  public SwissephException(String s) {
    super(s);
    type = UNDEFINED;
  }

  public int getType() {
    return type;
  }

  int getRC() {
    return rc;
  }
}
