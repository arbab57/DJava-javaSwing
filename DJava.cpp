#include <iostream>
#include <cstdlib>
#include <windows.h>

int main() {
    std::string javaProgramPath = "C:\\Users\\arbab84\\java\\DJava";
    std::string command = "cd " + javaProgramPath + " && java Main && exit";
    HWND window;
    AllocConsole();
    window = FindWindowA("ConsoleWindowClass", NULL);
    ShowWindow(window, 0);
    system(command.c_str());
    return 0;
}
