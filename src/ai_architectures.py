import torch
import torch.nn as nn
import torch.nn.functional as F

# =====================================================================
# Model: Convolutional Recurrent Neural Network (GreetingCRNN)
# Combines Conv2D spatial feature extraction with Bidirectional LSTM 
# for sequential temporal speech dynamics
# =====================================================================
class GreetingCRNN(nn.Module):
    def __init__(self, num_classes=7):
        super(GreetingCRNN, self).__init__()
        
        self.conv = nn.Sequential(
            nn.Conv2d(1, 32, kernel_size=3, stride=1, padding=1),
            nn.BatchNorm2d(32),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=(2, 1)), # pool frequency only
            
            nn.Conv2d(32, 64, kernel_size=3, stride=1, padding=1),
            nn.BatchNorm2d(64),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=(2, 1))  # pool frequency only
        )
        
        # 64 Mel bins reduced by factor of 4 = 16 frequency channels
        # Conv output shape: (Batch, 64_channels, 16_freq, Time_steps)
        self.lstm = nn.LSTM(
            input_size=64 * 16,
            hidden_size=128,
            num_layers=2,
            batch_first=True,
            bidirectional=True,
            dropout=0.3
        )
        
        self.fc = nn.Sequential(
            nn.Linear(128 * 2, 128), # Bidirectional (128 * 2)
            nn.ReLU(),
            nn.Dropout(0.3),
            nn.Linear(128, num_classes)
        )
        
    def forward(self, x):
        # x shape: (Batch, 1, Mel_bins, Time_steps)
        x = self.conv(x)
        
        # Reshape for LSTM: (Batch, Time_steps, Features)
        batch_size, channels, freq, time_steps = x.size()
        x = x.permute(0, 3, 1, 2).contiguous()
        x = x.view(batch_size, time_steps, channels * freq)
        
        # Pass through BiLSTM
        lstm_out, _ = self.lstm(x) # shape: (Batch, Time_steps, 256)
        
        # Temporal Average Pooling over time steps
        x = torch.mean(lstm_out, dim=1) # shape: (Batch, 256)
        
        logits = self.fc(x)
        return logits
