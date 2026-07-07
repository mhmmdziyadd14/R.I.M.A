import os
import torch
import torch.nn as nn
from torch.utils.data import DataLoader, random_split
import src.config as config
from src.dataset import GreetingDataset
from src.model import AudioCNN

def main():
    # Set device
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print(f"Using device: {device}")
    
    # Check if raw dataset folder exists and has files
    if not os.path.exists(config.DATASET_RAW_DIR):
        os.makedirs(config.DATASET_RAW_DIR, exist_ok=True)
        
    # Instantiate dataset
    full_dataset = GreetingDataset(
        data_dir=config.DATASET_RAW_DIR,
        classes=config.CLASSES,
        is_train=True
    )
    
    if len(full_dataset) == 0:
        print("\n" + "="*80)
        print("PERINGATAN: Dataset kosong!")
        print("Silakan rekam audio Anda dalam format WAV (16 kHz, mono, 1.5 detik) lalu masukkan")
        print("ke dalam sub-direktori yang sesuai di bawah ini:")
        print(f" -> {os.path.abspath(config.DATASET_RAW_DIR)}")
        print("\nSub-direktori kelas sapaan yang dibutuhkan:")
        for cls in config.CLASSES:
            print(f" - {cls}/")
        print("\nPastikan ada minimal beberapa file di tiap kategori sapaan, unknown, dan silence.")
        print("="*80 + "\n")
        return
        
    # Split into train/validation
    val_size = int(len(full_dataset) * config.VAL_SPLIT)
    train_size = len(full_dataset) - val_size
    
    # Prevent empty split error if dataset is very small
    if val_size == 0 and len(full_dataset) > 1:
        val_size = 1
        train_size = len(full_dataset) - 1
    elif len(full_dataset) == 1:
        val_size = 0
        train_size = 1
        
    if val_size > 0:
        train_dataset, val_dataset = random_split(full_dataset, [train_size, val_size])
        # Turn off data augmentation for validation set
        val_dataset.dataset.is_train = False
    else:
        train_dataset = full_dataset
        val_dataset = None
        
    # Create DataLoaders
    train_loader = DataLoader(train_dataset, batch_size=config.BATCH_SIZE, shuffle=True)
    val_loader = DataLoader(val_dataset, batch_size=config.BATCH_SIZE, shuffle=False) if val_dataset else None
    
    # Initialize model
    model = AudioCNN(num_classes=len(config.CLASSES)).to(device)
    
    # Loss and optimizer
    criterion = nn.CrossEntropyLoss()
    optimizer = torch.optim.Adam(model.parameters(), lr=config.LEARNING_RATE)
    
    best_acc = 0.0
    
    print("\nStarting Training...")
    for epoch in range(config.EPOCHS):
        model.train()
        train_loss = 0.0
        correct_train = 0
        total_train = 0
        
        for inputs, targets in train_loader:
            inputs, targets = inputs.to(device), targets.to(device)
            
            optimizer.zero_grad()
            outputs = model(inputs)
            loss = criterion(outputs, targets)
            loss.backward()
            optimizer.step()
            
            train_loss += loss.item() * inputs.size(0)
            _, predicted = outputs.max(1)
            total_train += targets.size(0)
            correct_train += predicted.eq(targets).sum().item()
            
        epoch_loss = train_loss / total_train
        epoch_acc = correct_train / total_train
        
        # Validation
        val_loss = 0.0
        val_acc = 0.0
        if val_loader:
            model.eval()
            correct_val = 0
            total_val = 0
            with torch.no_grad():
                for inputs, targets in val_loader:
                    inputs, targets = inputs.to(device), targets.to(device)
                    outputs = model(inputs)
                    loss = criterion(outputs, targets)
                    
                    val_loss += loss.item() * inputs.size(0)
                    _, predicted = outputs.max(1)
                    total_val += targets.size(0)
                    correct_val += predicted.eq(targets).sum().item()
            val_loss = val_loss / total_val
            val_acc = correct_val / total_val
            
            print(f"Epoch {epoch+1}/{config.EPOCHS} | Train Loss: {epoch_loss:.4f} | Train Acc: {epoch_acc*100:.2f}% | Val Loss: {val_loss:.4f} | Val Acc: {val_acc*100:.2f}%")
            
            # Save best model
            if val_acc >= best_acc:
                best_acc = val_acc
                torch.save(model.state_dict(), config.MODEL_SAVE_PATH)
                print(f" -> Saved new best model to {config.MODEL_SAVE_PATH}")
        else:
            print(f"Epoch {epoch+1}/{config.EPOCHS} | Train Loss: {epoch_loss:.4f} | Train Acc: {epoch_acc*100:.2f}%")
            torch.save(model.state_dict(), config.MODEL_SAVE_PATH)
            
    print("\nTraining completed!")
    print(f"Best Validation Accuracy: {best_acc*100:.2f}%")
    print(f"Model saved to: {config.MODEL_SAVE_PATH}")

if __name__ == "__main__":
    main()
