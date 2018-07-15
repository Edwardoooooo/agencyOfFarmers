package scau.zxck.web.admin;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import scau.zxck.base.dao.mybatis.Conditions;
import scau.zxck.base.exception.BaseException;
import scau.zxck.entity.market.*;
import scau.zxck.entity.sys.AdminInfo;
import scau.zxck.entity.sys.UserInfo;
import scau.zxck.service.sys.IAdminLoginService;
import scau.zxck.service.sys.IUserLoginService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by suruijia on 2016/2/6.
 */
@Controller
@RequestMapping("/")
public class LoginAction {
  @Autowired
  private IAdminLoginService adminLoginService;
  @Autowired
  private IUserLoginService userLoginService;

  /**
   * 获取分类
   * 
   * @return
   * @throws BaseException
   */
  @RequestMapping(value = "login", method = RequestMethod.POST)
  // 登录(已写入日志)
  public String login(String jsonStr) throws BaseException {
    String r = "";
    JSONObject data = JSON.parseObject(jsonStr);
    JSONObject temp = new JSONObject();

    if ((boolean) data.get("isAdmin")) {
      Conditions conditions = new Conditions();
      List list =
          adminLoginService.list(conditions.eq("admin_name", data.get("Admin_Name").toString()).or()
              .eq("admin_cell", data.get("Admin_Cell").toString()).or()
              .eq("admin_email", data.get("Admin_Email").toString()).and()
              .eq("admin_password", data.get("Admin_Password").toString()));
      if (list.isEmpty()) {
        temp.put("isCorrect", false);
      } else {
        temp.put("isCorrect", true);
        AdminInfo admin = (AdminInfo) list.get(0);
        temp.put("Admin_PK", admin.getId());
        temp.put("SignIn_IsAdmin", true);
      }

    } else {
      Conditions conditions = new Conditions();
      List list = null;
      list = userLoginService.list(conditions.eq("user_name", data.get("User_Name").toString()).or()
          .eq("user_cell", data.get("User_Cell").toString()).or()
          .eq("user_email", data.get("User_Email").toString()).and()
          .eq("user_password", data.get("User_Password").toString()));
      if (list.isEmpty()) {
        temp.put("isCorrect", false);
      } else {
        temp.put("isCorrect", true);
        UserInfo user = (UserInfo) list.get(0);
        temp.put("User_PK", user.getId());
        temp.put("SignIn_IsAdmin", false);
      }
    }

    if ((boolean) temp.get("isCorrect") == true) {
      // 登录日志
      temp.put("SignIn_Time", new Timestamp(System.currentTimeMillis()).toString());
      if ((boolean) temp.get("SignIn_IsAdmin") == true) {
        AdminInfo adminInfo = new AdminInfo();
        adminInfo.setAdmin_password(data.get("Admin_Password").toString());
        adminInfo.setAdmin_name(data.get("Admin_Name").toString());
        adminInfo.setAdmin_cell(data.get("Admin_Cell").toString());
        adminInfo.setAdmin_email(data.get("Admin_Email").toString());
        adminLoginService.add(adminInfo);
      } else {
        UserInfo userInfo = new UserInfo();

        userInfo.setUser_password(data.get("User_Password").toString());
        userInfo.setUser_name(data.get("User_Name").toString());
        userInfo.setUser_cell(data.get("User_Cell").toString());
        userInfo.setUser_email(data.get("User_Email").toString());
        userInfo.setUser_sex((int) Integer.parseInt(temp.get("User_Sex").toString()));
        userInfo.setUser_regtime(Timestamp.valueOf(temp.get("User_RegTime").toString()).toString());
        userInfo.setUser_realname(data.get("User_Realname").toString());
        userInfo.setUser_id(data.get("User_ID").toString());

        userInfo.setCart(new CartInfo());
        userInfo.setDeliveryaddress(new DeliveryAddress());
        userLoginService.add(userInfo);
      }
    }
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    HttpSession session = request.getSession();
    if ((boolean) temp.get("isCorrect")) {
      session.setAttribute("isAdmin", (boolean) data.get("isAdmin"));
      if ((boolean) data.get("isAdmin")) {
        session.setAttribute("Admin_PK", temp.get("Admin_PK"));
      } else {
        session.setAttribute("User_PK", temp.get("User_PK"));
      }
    }
    return "success";
  }
}