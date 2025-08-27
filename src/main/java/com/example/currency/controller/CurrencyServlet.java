package com.example.currency.controller;


import com.example.currency.dto.CurrencyDTO;
import com.example.currency.model.Currency;
import com.example.currency.service.CurrencyMapper;
import com.example.currency.service.CurrencyService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


@WebServlet("/currencies/*")
public class CurrencyServlet extends HttpServlet {

    private CurrencyService currencyService;
    private Gson gson;
    private CurrencyMapper currencyMapper;

    @Override
    public void init() throws ServletException {
        this.currencyService = new CurrencyService();
        this.gson = new Gson();
        this.currencyMapper = new CurrencyMapper();
    }

//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        String servletPath = request.getServletPath();
//        String pathInfo    = request.getPathInfo();
//
//        try {
//            if (servletPath.equals("/currencies")) {
//                List<Currency> all = currencyService.getAllCurrencies();
//                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, all);
//                return;
//            }
//
//            if ("/currency".equals(servletPath)) {
//                // Ожидаем /currency/{code}
//                if (pathInfo == null || pathInfo.length() <= 1) {
//                    JsonUtil.writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Код валюты отсутствует в адресе");
//                    return;
//                }
//                String code = pathInfo.substring(1);
//                Currency c = currencyService.getCurrencyByCode(code);
//                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, c);
//                return;
//            }
//
//            JsonUtil.writeError(response, HttpServletResponse.SC_NOT_FOUND, "Ресурс не найден");
//        }
//        catch (BadRequestException e) {
//            JsonUtil.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
//        }
//        catch (NotFoundException e) {
//            JsonUtil.writeError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
//        }
//        catch (Exception e) {
//            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка сервера");
//            e.printStackTrace();
//        }
//    }

//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            String name = request.getParameter("name");
//            String code = request.getParameter("code");
//            String sign = request.getParameter("sign");
//
//            Currency created = currencyService.addCurrency(name, code, sign);
//            CurrencyDTO currencyDTO = CurrencyMapper.toDTO(created);
//            JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED, currencyDTO);
//        } catch (BadRequestException e) {
//            JsonUtil.writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
//        } catch (ConflictException e) {
//            JsonUtil.writeError(response, HttpServletResponse.SC_CONFLICT, e.getMessage());
//        } catch (SQLException e) {
//            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка базы данных");
//            e.printStackTrace();
//        } catch (Exception e) {
//            JsonUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка сервера");
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Currency> currencies = currencyService.getAllCurrencies();

            List<CurrencyDTO> currencyDTOs = currencies.stream()
                    .map(CurrencyMapper::toDTO)
                    .collect(Collectors.toList());

            String json = gson.toJson(currencyDTOs);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Не удалось получить список валют\"}");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            CurrencyDTO currencyDTO = gson.fromJson(request.getReader(), CurrencyDTO.class);
            Currency currency = CurrencyMapper.fromDTO(currencyDTO);

            currencyService.addCurrency(currency);

            String json = gson.toJson(CurrencyMapper.toDTO(currency));

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().print(json);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Не удалось добавить валюту\"}");
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // 1. Достаём ID из URL /currencies/{id}
            String pathInfo = request.getPathInfo(); // например: "/5"
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"ID валюты не указан\"}");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(pathInfo.substring(1)); // убираем "/"
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Некорректный формат ID\"}");
                return;
            }

            // 2. Читаем тело запроса (JSON)
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String body = sb.toString();

            // 3. Преобразуем JSON → DTO
            CurrencyDTO dto = gson.fromJson(body, CurrencyDTO.class);

            // 4. Обновляем валюту через сервис
            Currency updated = currencyService.updateCurrency(id, dto.getCode(), dto.getFullName(), dto.getSign());

            // 5. Отправляем обновлённую валюту
            String json = gson.toJson(CurrencyMapper.toDTO(updated));
            response.setStatus(HttpServletResponse.SC_OK); // 200
            response.getWriter().write(json);

        } catch (IllegalArgumentException e) {
            // например: ID не найден или пустой код
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                // конфликт уникальности (дубликат code)
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409
                response.getWriter().write("{\"error\":\"Валюта с таким кодом уже существует\"}");
            } else {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
                response.getWriter().write("{\"error\":\"Ошибка базы данных\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().write("{\"error\":\"Некорректный запрос\"}");
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            /// 1. Достаём ID из URL /currencies/{id}
            String pathInfo = request.getPathInfo(); // например: "/5"
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"ID валюты не указан\"}");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(pathInfo.substring(1)); // убираем "/"
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Некорректный формат ID\"}");
                return;
            }

            // 2. Удаляем валюту
            boolean deleted = currencyService.deleteCurrency(id);

            if (deleted) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                response.getWriter().write("{\"error\":\"Валюта не найдена\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            response.getWriter().write("{\"error\":\"Ошибка при удалении валюты\"}");
        }
    }
}

