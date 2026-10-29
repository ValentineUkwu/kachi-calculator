import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class KachiCalculator implements ActionListener {

    JFrame frame;
    JTextField display;
    
    // Core Calculator Buttons
    JButton[] numButtons = new JButton[10];
    JButton[] funcButtons = new JButton[16];
    JButton addButton, subButton, mulButton, divButton;
    JButton decButton, equButton, delButton, clrButton;
    JButton sqrtButton, powButton, sqrButton, sinButton, cosButton, tanButton, logButton, factButton;
    
    // Tax & Financial Controls
    JTextField taxField;
    JButton addTaxBtn, subTaxBtn;
    
    // Currency Controls
    JComboBox<String> fromCurrencyBox, toCurrencyBox;
    JTextField currencyAmountField;
    JLabel currencyResultLabel;
    
    // Commodity / Stock Lookup Controls
    JComboBox<String> stockCombo;
    JLabel stockPriceLabel;

    Font mainFont = new Font("SansSerif", Font.BOLD, 18);
    Font subFont = new Font("SansSerif", Font.PLAIN, 14);

    double num1 = 0, num2 = 0, result = 0;
    char operator;

    // Exchange Rates relative to USD (Base)
    Map<String, Double> exchangeRates = new HashMap<>();
    
    // Reference Stock / Commodity Prices (USD)
    Map<String, String> stockData = new HashMap<>();

    public KachiCalculator() {
        initData();
        setupGUI();
    }

    private void initData() {
        // Approximate Exchange Rates (Base USD = 1.0)
        exchangeRates.put("USD (US)", 1.0);
        exchangeRates.put("GBP (UK)", 0.77);
        exchangeRates.put("CAD (Canada)", 1.38);
        exchangeRates.put("CNY (China)", 7.12);
        exchangeRates.put("JPY (Japan)", 152.5);
        exchangeRates.put("KRW (S. Korea)", 1380.0);
        exchangeRates.put("NGN (Nigeria)", 1650.0);
        exchangeRates.put("ZAR (S. Africa)", 17.6);
        exchangeRates.put("BRL (Brazil)", 5.70);
        exchangeRates.put("EUR (Germany)", 0.92);
        exchangeRates.put("RUB (Russia)", 97.0);

        // Commodities & Stocks (Ticker / Indicative Price USD)
        stockData.put("Gold (GC=F)", "$2,735.50 / oz");
        stockData.put("Silver (SI=F)", "$33.70 / oz");
        stockData.put("Crude Oil (CL=F)", "$70.50 / bbl");
        stockData.put("Apple (AAPL)", "$231.20");
        stockData.put("Microsoft (MSFT)", "$428.10");
        stockData.put("Alphabet (GOOGL)", "$167.50");
        stockData.put("Tesla (TSLA)", "$269.20");
    }

    private void setupGUI() {
        frame = new JFrame("Advanced Scientific & Financial Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 600);
        frame.setLayout(new BorderLayout(10, 10));

        // Display Panel
        display = new JTextField();
        display.setFont(new Font("SansSerif", Font.BOLD, 24));
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setPreferredSize(new Dimension(800, 60));
        frame.add(display, BorderLayout.NORTH);

        // Center Panel - Scientific Grid
        JPanel calcPanel = new JPanel(new GridLayout(6, 5, 8, 8));

        // Create Number Buttons
        for (int i = 0; i < 10; i++) {
            numButtons[i] = new JButton(String.valueOf(i));
            numButtons[i].setFont(mainFont);
            numButtons[i].addActionListener(this);
            numButtons[i].setFocusable(false);
        }

        // Standard Buttons
        addButton = new JButton("+");
        subButton = new JButton("-");
        mulButton = new JButton("*");
        divButton = new JButton("/");
        decButton = new JButton(".");
        equButton = new JButton("=");
        delButton = new JButton("DEL");
        clrButton = new JButton("CLR");

        // Scientific Buttons
        sqrtButton = new JButton("√");
        sqrButton = new JButton("x²");
        powButton = new JButton("xⁿ");
        sinButton = new JButton("sin");
        cosButton = new JButton("cos");
        tanButton = new JButton("tan");
        logButton = new JButton("log");
        factButton = new JButton("n!");

        funcButtons[0] = addButton; funcButtons[1] = subButton;
        funcButtons[2] = mulButton; funcButtons[3] = divButton;
        funcButtons[4] = decButton; funcButtons[5] = equButton;
        funcButtons[6] = delButton; funcButtons[7] = clrButton;
        funcButtons[8] = sqrtButton; funcButtons[9] = sqrButton;
        funcButtons[10] = powButton; funcButtons[11] = sinButton;
        funcButtons[12] = cosButton; funcButtons[13] = tanButton;
        funcButtons[14] = logButton; funcButtons[15] = factButton;

        for (JButton btn : funcButtons) {
            btn.setFont(mainFont);
            btn.addActionListener(this);
            btn.setFocusable(false);
        }

        // Layout Calculator Buttons into Grid
        calcPanel.add(sinButton); calcPanel.add(cosButton); calcPanel.add(tanButton); calcPanel.add(clrButton); calcPanel.add(delButton);
        calcPanel.add(logButton); calcPanel.add(sqrtButton); calcPanel.add(sqrButton); calcPanel.add(powButton); calcPanel.add(divButton);
        calcPanel.add(numButtons[7]); calcPanel.add(numButtons[8]); calcPanel.add(numButtons[9]); calcPanel.add(mulButton); calcPanel.add(factButton);
        calcPanel.add(numButtons[4]); calcPanel.add(numButtons[5]); calcPanel.add(numButtons[6]); calcPanel.add(subButton); calcPanel.add(decButton);
        calcPanel.add(numButtons[1]); calcPanel.add(numButtons[2]); calcPanel.add(numButtons[3]); calcPanel.add(addButton); calcPanel.add(numButtons[0]);
        calcPanel.add(equButton);

        frame.add(calcPanel, BorderLayout.CENTER);

        // Sidebar Panel - Financial, Tax & Currency Tools
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createTitledBorder("Financial Tools"));
        sidePanel.setPreferredSize(new Dimension(320, 500));

        // --- Tax Section ---
        JPanel taxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        taxPanel.setBorder(BorderFactory.createTitledBorder("Tax Calculator (%)"));
        taxField = new JTextField("20", 5);
        addTaxBtn = new JButton("+ Tax");
        subTaxBtn = new JButton("- Tax");
        addTaxBtn.addActionListener(this);
        subTaxBtn.addActionListener(this);
        taxPanel.add(new JLabel("Rate:"));
        taxPanel.add(taxField);
        taxPanel.add(addTaxBtn);
        taxPanel.add(subTaxBtn);
        sidePanel.add(taxPanel);

        // --- Currency Converter Section ---
        JPanel currPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        currPanel.setBorder(BorderFactory.createTitledBorder("Currency Exchange"));
        
        String[] currencies = exchangeRates.keySet().toArray(new String[0]);
        fromCurrencyBox = new JComboBox<>(currencies);
        toCurrencyBox = new JComboBox<>(currencies);
        toCurrencyBox.setSelectedItem("GBP (UK)");

        currencyAmountField = new JTextField("100");
        JButton convertBtn = new JButton("Convert Currency");
        currencyResultLabel = new JLabel("Result: ---", SwingConstants.CENTER);

        convertBtn.addActionListener(e -> convertCurrency());

        JPanel selectPanel = new JPanel(new GridLayout(1, 2));
        selectPanel.add(fromCurrencyBox);
        selectPanel.add(toCurrencyBox);

        currPanel.add(selectPanel);
        currPanel.add(currencyAmountField);
        currPanel.add(convertBtn);
        currPanel.add(currencyResultLabel);
        sidePanel.add(currPanel);

        // --- Stocks & Commodities Section ---
        JPanel stockPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        stockPanel.setBorder(BorderFactory.createTitledBorder("Stocks & Commodities"));
        
        String[] stocks = stockData.keySet().toArray(new String[0]);
        stockCombo = new JComboBox<>(stocks);
        stockPriceLabel = new JLabel("Price: Select Asset", SwingConstants.CENTER);
        
        stockCombo.addActionListener(e -> {
            String selected = (String) stockCombo.getSelectedItem();
            if (selected != null) {
                stockPriceLabel.setText("Est Price: " + stockData.get(selected));
            }
        });

        stockPanel.add(stockCombo);
        stockPanel.add(stockPriceLabel);
        sidePanel.add(stockPanel);

        frame.add(sidePanel, BorderLayout.EAST);
        frame.setVisible(true);
    }

    private void convertCurrency() {
        try {
            double amount = Double.parseDouble(currencyAmountField.getText());
            String from = (String) fromCurrencyBox.getSelectedItem();
            String to = (String) toCurrencyBox.getSelectedItem();

            double fromRate = exchangeRates.get(from);
            double toRate = exchangeRates.get(to);

            // Convert to base USD then to target currency
            double converted = (amount / fromRate) * toRate;
            currencyResultLabel.setText(String.format("Result: %.2f %s", converted, to.substring(0, 3)));
        } catch (Exception ex) {
            currencyResultLabel.setText("Invalid input!");
        }
    }

    private double calculateFactorial(int n) {
        if (n < 0) return Double.NaN;
        double fact = 1;
        for (int i = 1; i <= n; i++) fact *= i;
        return fact;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Number Input
        for (int i = 0; i < 10; i++) {
            if (e.getSource() == numButtons[i]) {
                display.setText(display.getText().concat(String.valueOf(i)));
            }
        }
        if (e.getSource() == decButton) {
            if (!display.getText().contains(".")) {
                display.setText(display.getText().concat("."));
            }
        }

        // Basic Math Operators
        if (e.getSource() == addButton) { num1 = Double.parseDouble(display.getText()); operator = '+'; display.setText(""); }
        if (e.getSource() == subButton) { num1 = Double.parseDouble(display.getText()); operator = '-'; display.setText(""); }
        if (e.getSource() == mulButton) { num1 = Double.parseDouble(display.getText()); operator = '*'; display.setText(""); }
        if (e.getSource() == divButton) { num1 = Double.parseDouble(display.getText()); operator = '/'; display.setText(""); }
        if (e.getSource() == powButton) { num1 = Double.parseDouble(display.getText()); operator = '^'; display.setText(""); }

        // Scientific Single-Operand Functions
        if (e.getSource() == sqrtButton) { display.setText(String.valueOf(Math.sqrt(Double.parseDouble(display.getText())))); }
        if (e.getSource() == sqrButton) { double val = Double.parseDouble(display.getText()); display.setText(String.valueOf(val * val)); }
        if (e.getSource() == sinButton) { display.setText(String.valueOf(Math.sin(Math.toRadians(Double.parseDouble(display.getText()))))); }
        if (e.getSource() == cosButton) { display.setText(String.valueOf(Math.cos(Math.toRadians(Double.parseDouble(display.getText()))))); }
        if (e.getSource() == tanButton) { display.setText(String.valueOf(Math.tan(Math.toRadians(Double.parseDouble(display.getText()))))); }
        if (e.getSource() == logButton) { display.setText(String.valueOf(Math.log10(Double.parseDouble(display.getText())))); }
        if (e.getSource() == factButton) { display.setText(String.valueOf(calculateFactorial((int) Double.parseDouble(display.getText())))); }

        // Tax Operations
        if (e.getSource() == addTaxBtn) {
            double val = Double.parseDouble(display.getText());
            double taxRate = Double.parseDouble(taxField.getText());
            display.setText(String.valueOf(val + (val * taxRate / 100)));
        }
        if (e.getSource() == subTaxBtn) {
            double val = Double.parseDouble(display.getText());
            double taxRate = Double.parseDouble(taxField.getText());
            display.setText(String.valueOf(val / (1 + (taxRate / 100))));
        }

        // Controls
        if (e.getSource() == clrButton) { display.setText(""); }
        if (e.getSource() == delButton) {
            String str = display.getText();
            if (str.length() > 0) display.setText(str.substring(0, str.length() - 1));
        }

        // Equals Operator
        if (e.getSource() == equButton) {
            num2 = Double.parseDouble(display.getText());
            switch (operator) {
                case '+': result = num1 + num2; break;
                case '-': result = num1 - num2; break;
                case '*': result = num1 * num2; break;
                case '/': result = (num2 != 0) ? num1 / num2 : 0; break;
                case '^': result = Math.pow(num1, num2); break;
            }
            display.setText(String.valueOf(result));
            num1 = result;
        }
    }

    public static void main(String[] args) {
        new KachiCalculator();
    }
}