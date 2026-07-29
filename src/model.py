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
            nn.MaxPool2d(kernel_size=(2, 1)), # Pooling freq, biarkan waktu utuh
            
            # Block 2
            nn.Conv2d(32, 64, kernel_size=3, stride=1, padding=1),
            nn.BatchNorm2d(64),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=(2, 1)),
        )
        
        # Setelah 2x MaxPool pada 64 mels -> tersisa 16 bins.
        # 64 channels * 16 bins = 1024 features per rentang waktu.
        self.lstm = nn.LSTM(input_size=1024, hidden_size=128, num_layers=2, batch_first=True, bidirectional=True, dropout=0.3)
        
        self.fc = nn.Sequential(
            nn.Linear(256, 128),
            nn.ReLU(),
            nn.Dropout(0.3),
            nn.Linear(128, num_classes)
        )
        
    def forward(self, x):
        # x shape: (batch, 1, 64 mels, time_steps)
        x = self.conv(x)
        
        # x shape: (batch, 64 channels, 16 bins, time_steps)
        batch, channels, h_out, w_out = x.size()
        
        # Reshape ke (batch, time_steps, channels * h_out)
        x = x.permute(0, 3, 2, 1).contiguous()
        x = x.view(batch, w_out, -1)
        
        # LSTM
        lstm_out, _ = self.lstm(x)
        
        # TEMPORAL AVERAGE POOLING (Merata-ratakan seluruh rentang waktu)
        x = torch.mean(lstm_out, dim=1)
        
        x = self.fc(x)
        return x
