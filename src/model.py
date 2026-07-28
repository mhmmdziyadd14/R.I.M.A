import torch
import torch.nn as nn

class AudioCNN(nn.Module):
    def __init__(self, num_classes):
        super(AudioCNN, self).__init__()
        
        self.conv = nn.Sequential(
            # Block 1
            nn.Conv2d(1, 32, kernel_size=3, stride=1, padding=1),
            nn.BatchNorm2d(32),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=2, stride=2),
            
            # Block 2
            nn.Conv2d(32, 64, kernel_size=3, stride=1, padding=1),
            nn.BatchNorm2d(64),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=2, stride=2),
            
            # Block 3
            nn.Conv2d(64, 128, kernel_size=3, stride=1, padding=1),
            nn.BatchNorm2d(128),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=2, stride=2)
        )
        
        # Adaptive pooling ensures a fixed size output regardless of exact time-step variations
        self.adaptive_pool = nn.AdaptiveAvgPool2d((4, 4)) # Output shape: (batch, 128, 4, 4)
        
        self.fc = nn.Sequential(
            nn.Linear(128 * 4 * 4, 256),
            nn.ReLU(),
            nn.Dropout(0.4),
            nn.Linear(256, num_classes)
        )
        
    def forward(self, x):
        x = self.conv(x)
        x = self.adaptive_pool(x)
        x = torch.flatten(x, 1)
        x = self.fc(x)
        return x

class AudioCRNN(nn.Module):
    def __init__(self, num_classes=7):
        super(AudioCRNN, self).__init__()
        
        self.conv = nn.Sequential(
            # Block 1
            nn.Conv2d(1, 32, kernel_size=3, stride=1, padding=1),
            nn.BatchNorm2d(32),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=2, stride=2),
            
            # Block 2
            nn.Conv2d(32, 64, kernel_size=3, stride=1, padding=1),
            nn.BatchNorm2d(64),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=2, stride=2),
        )
        
        # LSTM input size 1024, derived from H_in=64 (from N_MFCC=64)
        # after 2 maxpools -> H_out=16. 16 * 64 channels = 1024 features
        self.lstm = nn.LSTM(input_size=1024, hidden_size=128, num_layers=2, batch_first=True, bidirectional=True)
        
        self.fc = nn.Sequential(
            nn.Linear(256, 128),
            nn.ReLU(),
            nn.Dropout(0.4),
            nn.Linear(128, num_classes)
        )
        
    def forward(self, x):
        # x shape: (batch, 1, 64, time_steps)
        x = self.conv(x)
        
        # x shape after conv: (batch, 64, 16, time_steps_out)
        batch, channels, h_out, w_out = x.size()
        
        # Reshape to (batch, time_steps_out, channels * h_out)
        x = x.permute(0, 3, 2, 1).contiguous()
        x = x.view(batch, w_out, -1)
        
        lstm_out, _ = self.lstm(x)
        
        # Ambil state waktu terakhir
        x = lstm_out[:, -1, :]
        x = self.fc(x)
        return x
