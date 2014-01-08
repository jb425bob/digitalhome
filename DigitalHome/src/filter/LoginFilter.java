package filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sql.CRUD_SQL;


import com.alibaba.fastjson.JSONObject;

public class LoginFilter extends HttpServlet implements Filter{


	
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest)arg0;
		HttpServletResponse response = (HttpServletResponse)arg1;
		HttpSession session = request.getSession();
		String url = request.getServletPath();
		String contextPath = request.getContextPath();
		//System.out.println("do filter");
		//System.out.println(url);
		//System.out.println(url + "--------" + contextPath);
		if(url.equals("")) url +="/";
		String user = (String) session.getAttribute("user");
		//System.out.println("loginuser:"+user);

		if(user == null){
			if(url.startsWith("/main")||url.startsWith("/admin/")||url.startsWith("/family_members/")){
				System.out.println("用户没有登录，转到登录界面.");
				response.sendRedirect(contextPath + "/");
				return ;
			}			
		} 
		
		//System.out.println("user=" + user);
		arg2.doFilter(arg0, arg1);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}

}
