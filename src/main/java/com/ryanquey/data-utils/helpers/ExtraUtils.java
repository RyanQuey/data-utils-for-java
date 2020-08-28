package com.ryanquey.data-utils.helpers;

class ExtraUtils {
  // use like: ExtraUtils.spin("topic")
  // Try these next
  // https://stackoverflow.com/a/9302776/6952495
  private static int spinnerIndex = 0;
  private static String[] logos = {"\\", "|", "/", "-"};
  public static void spin (String topic) {
    try {
      String output = "polling " + topic + "..." + logos[spinnerIndex];
      // clear the last output
      System.out.print(Strings.repeat("\b", output.length()));
      System.out.print(output);
      // spin the spinner
      spinnerIndex ++;
      if (spinnerIndex == logos.length) {
        spinnerIndex = 0;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
