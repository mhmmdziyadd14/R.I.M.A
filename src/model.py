import torch
import torch.nn as nn
import torch.nn.functional as F

# =====================================================================
# Model 1: Standard 2D CNN (GreetingCNN)
# Fast, lightweight, efficient local feature extractor for Mel Spectrograms
# =====================================================================
class GreetingCNN(nn.Module):
    def __init__(self, num_classes=7):
        super(GreetingCNN, self).__init__()
        
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
        
        # Adaptive pooling forces the feature map to (8, 8) regardless of input audio length
        self.adaptive_pool = nn.AdaptiveAvgPool2d((8, 8))
        
        self.fc = nn.Sequential(
            nn.Linear(128 * 8 * 8, 256),
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


# =====================================================================
# Model 2: Convolutional Recurrent Neural Network (GreetingCRNN)
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


# =====================================================================
# Model 3: Residual Deep CNN (GreetingResNet)
# Uses Residual Blocks with skip connections to extract deep features
# =====================================================================
class ResidualBlock(nn.Module):
    def __init__(self, in_channels, out_channels, stride=1):
        super(ResidualBlock, self).__init__()
        self.conv1 = nn.Conv2d(in_channels, out_channels, kernel_size=3, stride=stride, padding=1, bias=False)
        self.bn1 = nn.BatchNorm2d(out_channels)
        self.conv2 = nn.Conv2d(out_channels, out_channels, kernel_size=3, stride=1, padding=1, bias=False)
        self.bn2 = nn.BatchNorm2d(out_channels)
        
        self.shortcut = nn.Sequential()
        if stride != 1 or in_channels != out_channels:
            self.shortcut = nn.Sequential(
                nn.Conv2d(in_channels, out_channels, kernel_size=1, stride=stride, bias=False),
                nn.BatchNorm2d(out_channels)
            )

    def forward(self, x):
        out = F.relu(self.bn1(self.conv1(x)))
        out = self.bn2(self.conv2(out))
        out += self.shortcut(x)
        out = F.relu(out)
        return out

class GreetingResNet(nn.Module):
    def __init__(self, num_classes=7):
        super(GreetingResNet, self).__init__()
        
        self.in_channels = 32
        self.conv1 = nn.Conv2d(1, 32, kernel_size=3, stride=1, padding=1, bias=False)
        self.bn1 = nn.BatchNorm2d(32)
        
        self.layer1 = self._make_layer(32, stride=1)
        self.layer2 = self._make_layer(64, stride=2)
        self.layer3 = self._make_layer(128, stride=2)
        
        self.global_pool = nn.AdaptiveAvgPool2d((1, 1))
        self.fc = nn.Sequential(
            nn.Linear(128, 64),
            nn.ReLU(),
            nn.Dropout(0.3),
            nn.Linear(64, num_classes)
        )
        
    def _make_layer(self, out_channels, stride):
        layers = []
        layers.append(ResidualBlock(self.in_channels, out_channels, stride))
        self.in_channels = out_channels
        layers.append(ResidualBlock(out_channels, out_channels, 1))
        return nn.Sequential(*layers)
        
    def forward(self, x):
        out = F.relu(self.bn1(self.conv1(x)))
        out = self.layer1(out)
        out = self.layer2(out)
        out = self.layer3(out)
        out = self.global_pool(out)
        out = torch.flatten(out, 1)
        out = self.fc(out)
        return out


def get_model(model_name="CNN", num_classes=7):
    """
    Factory function to instantiate models by name.
    Supported: 'CNN', 'CRNN', 'ResNet'
    """
    model_name = model_name.upper()
    if model_name == "CNN":
        return GreetingCNN(num_classes=num_classes)
    elif model_name == "CRNN":
        return GreetingCRNN(num_classes=num_classes)
    elif model_name in ["RESNET", "RESNET18"]:
        return GreetingResNet(num_classes=num_classes)
    else:
        raise ValueError(f"Unknown model architecture: {model_name}. Choose from 'CNN', 'CRNN', 'ResNet'.")
