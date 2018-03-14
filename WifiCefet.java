/**
* WifiCefet cria a rede WiFi FREERADIUS no Windows e no Linux
* ajustando os parâmetros necessários para o correto funcionamento
* <p>
* Para compilar basta executar:
* javac WifiCefet.java
*
* Para empacotar:
* jar cfvm WifiCefet.jar META-INF/MANIFEST.MF WifiCefet.class
* </p>
*
* @author      Rômulo Mendes Figueiredo <romulo.figueiredo@cefet-rj.br>
* @version     1.2018.03.14.1540
*/

import java.io.*;
import javax.swing.*;
import java.util.Scanner;

public class WifiCefet {

  public static String wifi;
  public static String xml;

  public static String titulo = "Rede WiFi CEFET/RJ";
	private static String OS = System.getProperty("os.name").toLowerCase();
  public static String versao = "1.2018.03.14.1540";

  public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}

	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}

  public static void exec(String comando) {
    try {

      System.out.println("\nExecutar comando:\n" + comando + '\n');
      Process p = Runtime.getRuntime().exec(comando);
      p.waitFor();

      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line = "";

      while ((line = reader.readLine())!= null) {
          System.out.println(line + "\n");
        }

    } catch(Exception e) {
      ////throw new Exception(e.getMessage());
      System.out.println(e.getMessage());
      JOptionPane.showMessageDialog(null, e.getMessage(), titulo, JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
  }

  public static void loadStrings() {

    xml = "<?xml version='1.0'?><WLANProfile xmlns='http://www.microsoft.com/networking/WLAN/profile/v1'><name>FREERADIUS</name><SSIDConfig><SSID><hex>46524545524144495553</hex><name>FREERADIUS</name></SSID><nonBroadcast>false</nonBroadcast></SSIDConfig><connectionType>ESS</connectionType><connectionMode>auto</connectionMode><autoSwitch>false</autoSwitch><MSM><security><authEncryption><authentication>WPA2</authentication><encryption>AES</encryption><useOneX>true</useOneX><FIPSMode xmlns='http://www.microsoft.com/networking/WLAN/profile/v2'>false</FIPSMode></authEncryption><PMKCacheMode>enabled</PMKCacheMode><PMKCacheTTL>720</PMKCacheTTL><PMKCacheSize>128</PMKCacheSize><preAuthMode>disabled</preAuthMode><OneX xmlns='http://www.microsoft.com/networking/OneX/v1'><cacheUserData>true</cacheUserData><authMode>user</authMode><EAPConfig><EapHostConfig xmlns='http://www.microsoft.com/provisioning/EapHostConfig'><EapMethod><Type xmlns='http://www.microsoft.com/provisioning/EapCommon'>25</Type><VendorId xmlns='http://www.microsoft.com/provisioning/EapCommon'>0</VendorId><VendorType xmlns='http://www.microsoft.com/provisioning/EapCommon'>0</VendorType><AuthorId xmlns='http://www.microsoft.com/provisioning/EapCommon'>0</AuthorId></EapMethod><Config xmlns='http://www.microsoft.com/provisioning/EapHostConfig'><Eap xmlns='http://www.microsoft.com/provisioning/BaseEapConnectionPropertiesV1'><Type>25</Type><EapType xmlns='http://www.microsoft.com/provisioning/MsPeapConnectionPropertiesV1'><ServerValidation><DisableUserPromptForServerValidation>false</DisableUserPromptForServerValidation><ServerNames></ServerNames></ServerValidation><FastReconnect>true</FastReconnect><InnerEapOptional>false</InnerEapOptional><Eap xmlns='http://www.microsoft.com/provisioning/BaseEapConnectionPropertiesV1'><Type>26</Type><EapType xmlns='http://www.microsoft.com/provisioning/MsChapV2ConnectionPropertiesV1'><UseWinLogonCredentials>false</UseWinLogonCredentials></EapType></Eap><EnableQuarantineChecks>false</EnableQuarantineChecks><RequireCryptoBinding>false</RequireCryptoBinding><PeapExtensions><PerformServerValidation xmlns='http://www.microsoft.com/provisioning/MsPeapConnectionPropertiesV2'>false</PerformServerValidation><AcceptServerName xmlns='http://www.microsoft.com/provisioning/MsPeapConnectionPropertiesV2'>false</AcceptServerName></PeapExtensions></EapType></Eap></Config></EapHostConfig></EAPConfig></OneX></security></MSM></WLANProfile>";

    //nmcli connection add type wifi ifname '*' ssid 'FREERADIUS' mode infrastructure -- wifi-sec.key-mgmt wpa-eap 802-1x.eap peap 802-1x.identity username 802-1x.phase2-auth mschapv2 ipv4.method auto wifi.mtu auto ipv6.method ignore
    wifi = "[connection]";
    wifi += "id=FREERADIUS";
    wifi += "type=802-11-wireless";
    wifi += "permissions=";
    wifi += "secondaries=";

    wifi += "[wifi]";
    wifi += "mac-address-blacklist=";
    wifi += "mac-address-randomization=0";
    wifi += "mode=infrastructure";
    wifi += "seen-bssids=";
    wifi += "ssid=FREERADIUS";

    wifi += "[wifi-security]";
    wifi += "group=";
    wifi += "key-mgmt=wpa-eap";
    wifi += "pairwise=";
    wifi += "proto=";

    wifi +="[802-1x]";
    wifi += "altsubject-matches=";
    wifi += "eap=peap;";
    wifi += "identity=$_IDENTITY";
    wifi += "phase2-altsubject-matches=";
    wifi += "phase2-auth=mschapv2";

    wifi += "[ipv4]";
    wifi += "dns-search=";
    wifi += "method=auto";

    wifi += "[ipv6]";
    wifi += "addr-gen-mode=stable-privacy";
    wifi += "dns-search=";
    wifi += "method=ignore";

  }

  public static void main(String[] args) {

    loadStrings();

    String comando;
    String username;
    String password;

    try {
      System.out.println("");
      System.out.println("Programa para criar rede Wi-Fi do CEFET/RJ utilizando o Radius");
      System.out.println("Versão: " + versao);
      System.out.println("");
      System.out.println("Sistema Operacional Informado pelo Sistema: " + OS + "\n");

      /* LINUX */
      if (isUnix()) {
        System.out.println("Sistema Operacional detectado: Linux/Unix/AIX");

        System.out.println("Abrindo janela para informar nome do usuário...");
        username = JOptionPane.showInputDialog(null, "Informe o NOME DO USUÁRIO na rede Wi-Fi: ", titulo, JOptionPane.QUESTION_MESSAGE);
        if (username == null) {
          throw new Exception("Cancelado pelo usuário.");
        };

        System.out.println("Abrindo janela para informar senha do usuário...");
        password = JOptionPane.showInputDialog(null, "Informe a SENHA do usuário na rede Wi-Fi: ", titulo, JOptionPane.QUESTION_MESSAGE);
        if (password == null) {
          throw new Exception("Cancelado pelo usuário.");
        };

        exec("nmcli connections delelele delete FREERADIUS");

        comando = "nmcli connection add type 802-11-wireless con-name FREERADIUS ifname * ssid FREERADIUS mode infrastructure ";
        comando += "-- wifi-sec.key-mgmt wpa-eap 802-1x.eap peap 802-1x.identity " + username + " 802-1x.password " + password + " 802-1x.phase2-auth mschapv2 ipv4.method auto wifi.mtu auto ipv6.method ignore";
        exec(comando);

      } else if (isWindows()) {

          System.out.println("Sistema Operacional detectado: Windows");

          File file = File.createTempFile("wificefet", ".xml");
          //file.deleteOnExit();
      		System.out.println("Caminho e nome do arquivo temporário : " + file.getAbsolutePath());
          BufferedWriter bw = new BufferedWriter(new FileWriter(file));
          bw.write(xml);
          bw.close();

          comando = "netsh wlan add profile filename=\"" + file.getAbsolutePath() + "\"";
          exec(comando);
      } else {
        System.out.println("Erro: sistema operacional não detectado.");
        JOptionPane.showMessageDialog(null, "Sistema operacional não detectado ou não compatível.", titulo, JOptionPane.ERROR_MESSAGE);
        throw new Exception("SO_NAO_DETECTADO");
      };

      JOptionPane.showMessageDialog(null, "Rede Wi-Fi criada com sucesso!\nPode ser necessário reiniciar a máquina.");

    } catch (Exception e) {
      System.out.println("erro.....");
      e.printStackTrace();
    }
  }
}
