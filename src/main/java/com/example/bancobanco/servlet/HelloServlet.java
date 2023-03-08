package com.example.bancobanco.servlet;

import java.io.*;
import java.util.List;

import com.example.bancobanco.dao.Dao;
import com.example.bancobanco.dao.HibernateDao;
import com.example.bancobanco.hibernate.HibernateUtil;
import com.example.bancobanco.beans.Clients;
import com.example.bancobanco.beans.Comptes;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

@WebServlet(name = "helloServlet", value = "/Create.do")
public class HelloServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if (accion.equals("crear")){
            int condicion = 0;
            String dni = req.getParameter("idFiscal");
            String nombre = req.getParameter("clienteNombre");
            String email = req.getParameter("clienteEmail");
            String pais = req.getParameter("clientePais");
            String cuenta = req.getParameter("clienteCuenta");
            String ingresoString = req.getParameter("clienteIngreso");
            int ingreso = Integer.parseInt(ingresoString);

            Clients clienteExistente = null;
            Session session = HibernateUtil.getSessionFactory();
            Transaction transaction = null;

            try {
                transaction = session.beginTransaction();
                clienteExistente = session.get(Clients.class, dni);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            } finally {
                session.close();
            }

            if (clienteExistente == null){
                clienteExistente = new Clients(dni, nombre, email, pais);
                condicion++;
            }

            Comptes comptes = new Comptes(cuenta, ingreso, clienteExistente);
            clienteExistente.getCompteByIdCompte().add(comptes);
            System.out.println(clienteExistente.toString());
            session = HibernateUtil.getSessionFactory();
            transaction = null;
            try {
                transaction = session.beginTransaction();
                session.merge(clienteExistente);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            } finally {
                session.close();
            }

            resp.setContentType("text/plain");
            PrintWriter out = resp.getWriter();
            switch (condicion){
                case 0:
                    out.println("Añadido cuenta bancaria a usuario " + nombre + " con éxito");
                    System.out.println("se ha creado");
                    break;
                case 1:
                    out.println("Creación usuario " + nombre + " con éxito");
                    System.out.println("se ha creado");
                    break;
            }
            out.close();
        }
        else if (accion.equals("mostrar")){
            List<Clients> clients;
            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();

            Dao dao = new HibernateDao(HibernateUtil.getSessionFactory().getSessionFactory());

            clients = dao.getAll();

            out.println("<html>");
            out.println("<body>");
            out.println("<h1>Clientes y sus cuentas</h1>");
            out.println("<table>");
            out.println("<tr><th>Nombre</th><th>DNI</th><th>Cuenta</th><th>Saldo</th></tr>");

            for(Clients client : clients) {
                out.println("<tr>");
                out.println("<td>" + client.getNombre() + "</td>");
                out.println("<td>" + client.getDni() + "</td>");

                List<Comptes> comptes = client.getCompteByIdCompte();

                for (Comptes compte : comptes) {
                    out.println("<td>" + compte.getCuenta() + "</td>");
                    out.println("<td>" + compte.getIngresoInicial() + "</td>");
                }
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("</html>");
            out.println("</body>");
        }
    }
}