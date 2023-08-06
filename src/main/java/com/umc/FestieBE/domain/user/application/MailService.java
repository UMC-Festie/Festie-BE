//package com.umc.FestieBE.domain.user.application;
//
//import com.umc.FestieBE.domain.user.dao.UserRepository;
//import com.umc.FestieBE.domain.user.domain.User;
//import com.umc.FestieBE.domain.user.dto.MailDto;
//import com.umc.FestieBE.domain.user.dto.UserSignUpRequestDto;
//import lombok.AllArgsConstructor;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//
//import java.util.Optional;
//
//@AllArgsConstructor
//public class MailService {
//    private final User user;
//    private final UserRepository userRepository;
//    private final UserSignUpRequestDto requestDto;
//    private JavaMailSender javaMailSender;
//
//    public MailDto createMailAndChangePassword(String email) {
//        String str = getTempPassword();
//        MailDto mailDto = new MailDto();
//        mailDto.setEmailCheck(email);
//        mailDto.setTitle("Festie 임시비밀번호 안내 이메일 입니다.");
//        mailDto.setMessage("안녕하세요. Festie 임시비밀번호 안내 관련 이메일 입니다." + " 회원님의 임시 비밀번호는 "
//                + str + " 입니다." + "로그인 후에 비밀번호를 변경을 해주세요");
//        updatePassword(str, email);
//        return mailDto;
//    }
//
//
//    public void updatePassword(String str, String email){
//        String memberPassword = str;
//        Long memberId = userRepository.findByEmail(email).getId();
//
//        updatePassword(memberId,memberPassword);
//    }
//
//    public String getTempPassword(){
//        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
//                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
//
//        String str = "";
//
//        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
//        int idx = 0;
//        for (int i = 0; i < 10; i++) {
//            idx = (int) (charSet.length * Math.random());
//            str += charSet[idx];
//        }
//        return str;
//    }
//
//    @Override
//    public void mailSend(MailDto mailDto) {
//        System.out.println("전송 완료!");
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(mailDto.getAddress());
//        message.setSubject(mailDto.getTitle());
//        message.setText(mailDto.getMessage());
//        message.setFrom("보낸이@naver.com");
//        message.setReplyTo("보낸이@naver.com");
//        System.out.println("message"+message);
//        mailSender.send(message);
//    }
//
//    @Override
//    public void updatePassWord(Long id, String password) {
//        mmr.updatePassword(id,password);
//    }
//
//
//}
//
//
//
