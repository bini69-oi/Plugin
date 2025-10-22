import java.io.*;
import java.util.*;

public class PluginD {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Использование: java PluginD файл.cpp");
            return;
        }

        String filename = args[0];
        analyzeFile(filename);
    }

    public static void analyzeFile(String filename) {
        try {
            List<String> lines = readFile(filename);
            List<String> allVars = new ArrayList<>();
            List<String> unusedVars = new ArrayList<>();

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (line.startsWith("//") || line.isEmpty()) {
                    continue;
                }

                if ((line.contains("int ") || line.contains("double ") || line.contains("float ") || line.contains("string ")) && line.contains(";")) {
                    String varName = findVarName(line);
                    if (varName != null) {
                        allVars.add(varName);

                        boolean used = false;
                        for (int j = 0; j < lines.size(); j++) {
                            if (j != i) {
                                String checkLine = lines.get(j);
                                if (checkLine.contains(varName) && !isDeclaration(checkLine, varName)) {
                                    used = true;
                                    break;
                                }
                            }
                        }

                        if (!used) {
                            unusedVars.add(varName);
                        }
                    }
                }
            }

            System.out.println("=== Анализ файла: " + filename + " ===");
            System.out.println("Всего переменных: " + allVars.size());

            if (!unusedVars.isEmpty()) {
                System.out.println("Неиспользуемые переменные:");
                for (String var : unusedVars) {
                    System.out.println("⚠️  " + var);
                }
            } else {
                System.out.println("✅ Все переменные используются!");
            }

            System.out.println("📊 Статистика:");
            System.out.println("• Используемых: " + (allVars.size() - unusedVars.size()));
            System.out.println("• Неиспользуемых: " + unusedVars.size());

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static List<String> readFile(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines;
    }

    private static String findVarName(String line) {
        line = line.split("//")[0].trim();
        if (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1);
        }

        String[] parts = line.split("\\s+");
        for (String word : parts) {
            String cleanWord = word.trim();
            if (cleanWord.matches("[a-zA-Z_][a-zA-Z0-9_]*") &&
                !cleanWord.equals("int") &&
                !cleanWord.equals("double") &&
                !cleanWord.equals("float") &&
                !cleanWord.equals("string") &&
                !cleanWord.equals("main")) {
                return cleanWord;
            }
        }
        return null;
    }

    private static boolean isDeclaration(String line, String varName) {
        return line.contains("int " + varName) ||
               line.contains("double " + varName) ||
               line.contains("float " + varName) ||
               line.contains("string " + varName);
    }
}
