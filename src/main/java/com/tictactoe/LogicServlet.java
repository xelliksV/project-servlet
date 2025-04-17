package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int index = getIndex(req);

        HttpSession session = req.getSession();

        Field field = extractField(session);
        Sign sign = field.getField().get(index);
        if (Sign.EMPTY != sign) {
            getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }
        field.getField().put(index, Sign.CROSS);
        if (checkWin(resp, session, field)) {
            return;
        }
        int emptyFieldIndex = field.getEmptyFieldIndex();

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if (checkWin(resp, session, field)) {
                return;
            }
        } else {
            session.setAttribute("draw", true);
            List<Sign> fieldData = field.getFieldData();
            session.setAttribute("data", fieldData);
            resp.sendRedirect("/index.jsp");
            return;
        }
        List<Sign> fieldData = field.getFieldData();

        session.setAttribute("field", field);
        session.setAttribute("data", fieldData);

        resp.sendRedirect("/index.jsp");
    }
    private Field extractField(HttpSession session) {
        Object field = session.getAttribute("field");
        if (Field.class != field.getClass()) {
            session.invalidate();
            throw new RuntimeException("Session is broken");
        }
        return (Field) field;
    }
    private int getIndex(HttpServletRequest req) {
        String index = req.getParameter("click");
        boolean isValid = index.chars().allMatch(Character::isDigit);
        return isValid ? Integer.parseInt(index) : 0;
    }
    private boolean checkWin(HttpServletResponse resp, HttpSession session, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            session.setAttribute("winner", winner);
            List<Sign> fieldData = field.getFieldData();
            session.setAttribute("data", fieldData);
            resp.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
