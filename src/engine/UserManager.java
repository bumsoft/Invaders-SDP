package engine;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class UserManager {
    private String sessionCookie;
    private Logger logger = Core.getLogger();
    private HttpURLConnection connection;

    public boolean register(String id, String pw)
    {
        try {
            // URL 설정
            URL url = new URL(Core.getServerUrl()+"register");
            connection = (HttpURLConnection) url.openConnection();

            // HTTP 설정
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            //쉽게 json으로 보내기위해 jackson 사용함
            RegisterDto registerDto = new RegisterDto(id, pw);

            // Jackson ObjectMapper를 사용해 객체를 JSON으로
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInputString = objectMapper.writeValueAsString(registerDto);

            // 데이터 전송
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            logger.info("Response Code: " + responseCode);

            // 응답 본문 읽기
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                logger.info("Registered");
                return true;
            }
            else
            {
                logger.info("Register failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            connection.disconnect();
        }
        return false;
    }

    public void login(String id, String pw)
    {
        try {
            // URL 설정
            URL url = new URL(Core.getServerUrl()+"login");
            connection = (HttpURLConnection) url.openConnection();

            // HTTP 설정
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);


            String urlParameters = "username=" + URLEncoder.encode(id, "UTF-8") +
                    "&password=" + URLEncoder.encode(pw, "UTF-8");

            // username=id1&password=pw1 형식으로 보냄.

            // 데이터 전송
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = urlParameters.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            logger.info("Response Code: " + responseCode); //성공:200 실패:401로 서버에서 설정해둠

            // 응답 본문 읽기
            if (responseCode == HttpURLConnection.HTTP_OK) { //200
                logger.info("Login succeeded");

                //로그인 성공시 서버에서 세션 쿠키가 반환됨. 이걸 보관하며, 모든 요청에 이걸 포함에서 보내야 로그인된 사용자라는걸 서버가 알 수 있음.
                String setCookieHeader = connection.getHeaderField("Set-Cookie");
                if (setCookieHeader != null) {
                    this.sessionCookie = setCookieHeader.split(";")[0];
                    logger.info("sessionCookie saved.");
                }
                else {
                    //세션쿠키 다시 받는 처리(재로그인?)
                    logger.info("sessionCookie not exists.");
                }
            } else { //401
                //로그인 실패에 대한 처리
                logger.info("Login failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally
        {
            connection.disconnect();
        }
    }

    private class RegisterDto {
        private String username;
        private String password;

        // 생성자
        public RegisterDto(String username, String password) {
            this.username = username;
            this.password = password;
        }

        // Getter와 Setter (생략 가능)
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}


