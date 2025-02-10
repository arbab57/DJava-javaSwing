#include <iostream>
#include <cstdlib>
#include <windows.h>
#include <shlwapi.h>
#include <filesystem>

int main() {
    char path[MAX_PATH];
    if (GetModuleFileNameA(NULL, path, MAX_PATH)) {
    std::string javaProgramPath = std::filesystem::path(path).parent_path().string();
    std::string command = "cd " + javaProgramPath + " && java Main && exit";
    HWND window;
    AllocConsole();
    window = FindWindowA("ConsoleWindowClass", NULL);
    ShowWindow(window, 0);
    system(command.c_str());
    } else {
        std::cerr << "Error retrieving launch directory." << std::endl;
        system("pause");
    }





    return 0;
}
