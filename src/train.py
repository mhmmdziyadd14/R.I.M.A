import os
import sys
# Add project root to sys.path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import time
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import StratifiedShuffleSplit
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score

import torch
import torch.nn as nn
from torch.utils.data import DataLoader, Subset

import src.config as config
from src.dataset import GreetingDataset, pad_collate_fn
from src.model import get_model

def count_parameters(model):
    """Counts trainable parameters in PyTorch model."""
    return sum(p.numel() for p in model.parameters() if p.requires_grad)

def train_single_model(model_name, train_loader, val_loader, device):
    """Trains a single model architecture and logs training history."""
    print("\n" + "="*70)
    print(f" MULAI TRAINING MODEL: {model_name} ")
    print("="*70)
    
    model = get_model(model_name, num_classes=len(config.CLASSES)).to(device)
    num_params = count_parameters(model)
    print(f"Arsitektur: {model_name} | Total Parameter: {num_params:,}")
    
    criterion = nn.CrossEntropyLoss()
    optimizer = torch.optim.Adam(model.parameters(), lr=config.LEARNING_RATE, weight_decay=1e-4)
    scheduler = torch.optim.lr_scheduler.ReduceLROnPlateau(optimizer, mode='max', factor=0.5, patience=4)
    
    history = {
        "train_loss": [], "train_acc": [],
        "val_loss": [], "val_acc": []
    }
    
    best_val_acc = 0.0
    model_save_path = os.path.join(config.MODELS_DIR, f"best_model_{model_name}.pth")
    start_time = time.time()
    
    for epoch in range(config.EPOCHS):
        # --- Training Phase ---
        model.train()
        running_loss = 0.0
        correct_train = 0
        total_train = 0
        
        for inputs, targets in train_loader:
            inputs, targets = inputs.to(device), targets.to(device)
            
            optimizer.zero_grad()
            outputs = model(inputs)
            loss = criterion(outputs, targets)
            loss.backward()
            optimizer.step()
            
            running_loss += loss.item() * inputs.size(0)
            _, predicted = outputs.max(1)
            total_train += targets.size(0)
            correct_train += predicted.eq(targets).sum().item()
            
        epoch_train_loss = running_loss / total_train
        epoch_train_acc = (correct_train / total_train) * 100
        
        # --- Validation Phase ---
        model.eval()
        running_val_loss = 0.0
        correct_val = 0
        total_val = 0
        
        with torch.no_grad():
            for inputs, targets in val_loader:
                inputs, targets = inputs.to(device), targets.to(device)
                outputs = model(inputs)
                loss = criterion(outputs, targets)
                
                running_val_loss += loss.item() * inputs.size(0)
                _, predicted = outputs.max(1)
                total_val += targets.size(0)
                correct_val += predicted.eq(targets).sum().item()
                
        epoch_val_loss = running_val_loss / total_val
        epoch_val_acc = (correct_val / total_val) * 100
        
        scheduler.step(epoch_val_acc)
        
        history["train_loss"].append(epoch_train_loss)
        history["train_acc"].append(epoch_train_acc)
        history["val_loss"].append(epoch_val_loss)
        history["val_acc"].append(epoch_val_acc)
        
        print(f"Epoch [{epoch+1:02d}/{config.EPOCHS:02d}] | Train Loss: {epoch_train_loss:.4f} | Train Acc: {epoch_train_acc:6.2f}% | Val Loss: {epoch_val_loss:.4f} | Val Acc: {epoch_val_acc:6.2f}%")
        
        # Save best checkpoint
        if epoch_val_acc >= best_val_acc:
            best_val_acc = epoch_val_acc
            torch.save(model.state_dict(), model_save_path)
            
    training_time = time.time() - start_time
    print(f"Training {model_name} selesai dalam {training_time:.2f} detik. Best Val Acc: {best_val_acc:.2f}%")
    
    # Load best model for evaluation
    model.load_state_dict(torch.load(model_save_path, map_location=device))
    return model, history, best_val_acc, training_time, num_params

def evaluate_model_predictions(model, val_loader, device):
    """Evaluates best model checkpoint on validation set to collect true/pred labels."""
    model.eval()
    all_preds = []
    all_targets = []
    
    with torch.no_grad():
        for inputs, targets in val_loader:
            inputs = inputs.to(device)
            outputs = model(inputs)
            _, predicted = outputs.max(1)
            
            all_preds.extend(predicted.cpu().numpy())
            all_targets.extend(targets.numpy())
            
    return np.array(all_targets), np.array(all_preds)

def plot_confusion_matrix(y_true, y_pred, model_name):
    """Generates and saves a confusion matrix heatmap."""
    cm = confusion_matrix(y_true, y_pred)
    plt.figure(figsize=(8, 6))
    sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', 
                xticklabels=config.CLASSES, yticklabels=config.CLASSES)
    plt.title(f'Confusion Matrix - {model_name}')
    plt.xlabel('Predicted Label')
    plt.ylabel('True Label')
    plt.tight_layout()
    save_path = os.path.join(config.OUTPUT_DIR, f"confusion_matrix_{model_name}.png")
    plt.savefig(save_path, dpi=300)
    plt.close()
    print(f"Confusion Matrix disimpan ke: {save_path}")

def plot_comparison_curves(histories):
    """Plots Train/Val Loss and Accuracy comparison curves for all models."""
    plt.figure(figsize=(14, 5))
    
    # Plot Accuracy
    plt.subplot(1, 2, 1)
    for model_name, hist in histories.items():
        plt.plot(hist["val_acc"], label=f"{model_name} (Val)")
        plt.plot(hist["train_acc"], linestyle='--', alpha=0.5, label=f"{model_name} (Train)")
    plt.title('Comparison: Model Accuracy per Epoch')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy (%)')
    plt.legend()
    plt.grid(True, alpha=0.3)
    
    # Plot Loss
    plt.subplot(1, 2, 2)
    for model_name, hist in histories.items():
        plt.plot(hist["val_loss"], label=f"{model_name} (Val)")
        plt.plot(hist["train_loss"], linestyle='--', alpha=0.5, label=f"{model_name} (Train)")
    plt.title('Comparison: Model Loss per Epoch')
    plt.xlabel('Epoch')
    plt.ylabel('Loss (CrossEntropy)')
    plt.legend()
    plt.grid(True, alpha=0.3)
    
    plt.tight_layout()
    save_path = os.path.join(config.OUTPUT_DIR, "model_comparison_plots.png")
    plt.savefig(save_path, dpi=300)
    plt.close()
    print(f"Plot Komparatif disimpan ke: {save_path}")

def main():
    os.makedirs(config.MODELS_DIR, exist_ok=True)
    os.makedirs(config.OUTPUT_DIR, exist_ok=True)
    
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print(f"PyTorch Device: {device}")
    
    # Load full dataset
    print(f"Membaca dataset dari: {config.DATASET_DIR}...")
    full_dataset = GreetingDataset(
        data_dir=config.DATASET_DIR,
        classes=config.CLASSES,
        is_train=True
    )
    
    if len(full_dataset) == 0:
        print("ERROR: Tidak ada file audio yang ditemukan di Dataset.")
        return
        
    # Stratified Train / Validation Split (80% Train, 20% Val)
    labels = np.array(full_dataset.labels)
    sss = StratifiedShuffleSplit(n_splits=1, test_size=config.VAL_SPLIT, random_state=config.RANDOM_SEED)
    train_indices, val_indices = next(sss.split(np.zeros(len(labels)), labels))
    
    train_subset = Subset(full_dataset, train_indices)
    val_subset = Subset(full_dataset, val_indices)
    
    print(f"Dataset Split -> Total: {len(full_dataset)} | Train: {len(train_subset)} | Validation: {len(val_subset)}")
    
    train_loader = DataLoader(
        train_subset, 
        batch_size=config.BATCH_SIZE, 
        shuffle=True, 
        collate_fn=pad_collate_fn
    )
    val_loader = DataLoader(
        val_subset, 
        batch_size=config.BATCH_SIZE, 
        shuffle=False, 
        collate_fn=pad_collate_fn
    )
    
    # Models to train and compare
    models_to_train = ["CNN", "CRNN", "ResNet"]
    histories = {}
    results_summary = []
    
    best_overall_acc = -1.0
    best_overall_model_name = ""
    
    for model_name in models_to_train:
        model, hist, best_val_acc, t_time, num_params = train_single_model(
            model_name, train_loader, val_loader, device
        )
        histories[model_name] = hist
        
        # Evaluate Best Checkpoint
        y_true, y_pred = evaluate_model_predictions(model, val_loader, device)
        plot_confusion_matrix(y_true, y_pred, model_name)
        
        report = classification_report(y_true, y_pred, target_names=config.CLASSES, output_dict=True)
        macro_f1 = report["macro avg"]["f1-score"] * 100
        
        results_summary.append({
            "name": model_name,
            "val_acc": best_val_acc,
            "macro_f1": macro_f1,
            "params": num_params,
            "train_time": t_time
        })
        
        # Save champion model
        if best_val_acc > best_overall_acc:
            best_overall_acc = best_val_acc
            best_overall_model_name = model_name
            overall_save_path = os.path.join(config.MODELS_DIR, "best_overall_model.pth")
            torch.save(model.state_dict(), overall_save_path)
            
    # Plot comparison curves
    plot_comparison_curves(histories)
    
    # Print Final Comparative Summary Table
    print("\n" + "="*80)
    print(" TABEL RINGKASAN PERBANDINGAN MODEL SALAM DAERAH ")
    print("="*80)
    print(f"{'Nama Model':<15} | {'Val Accuracy':<15} | {'Macro F1-Score':<15} | {'Total Params':<15} | {'Waktu Training':<15}")
    print("-" * 80)
    for res in results_summary:
        print(f"{res['name']:<15} | {res['val_acc']:6.2f}%         | {res['macro_f1']:6.2f}%         | {res['params']:<15,} | {res['train_time']:6.2f}s")
    print("="*80)
    print(f"\n MODEL JUARA TERBAIK: {best_overall_model_name} dengan Akurasi Validation {best_overall_acc:.2f}%!")
    print(f" Model disimpan di: {os.path.join(config.MODELS_DIR, 'best_overall_model.pth')}")
    print("="*80 + "\n")

if __name__ == "__main__":
    main()
